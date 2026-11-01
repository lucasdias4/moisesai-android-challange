---
name: add-data-endpoint
description: Take a remote endpoint or a Room source through the data layers (probe the real API, DTOs, Retrofit service, datasource, mapper, repository, use case, tests) following this project's conventions. Use when a new or changed API response must reach the app, or when writing or auditing a datasource or repository.
---

The procedure for taking an endpoint to the UI. **The rules are in `ARCHITECTURE.md`** (§3 vocabulary, §6 data layer, §8 Clean Code, §9 testing and verification, §10 the iTunes facts); this skill says in what order to do things and links to them instead of repeating them.

Ownership by module: DTOs, the Retrofit service, extensions and remote datasources in `:core:network`; `Db*` entities, DAOs and local datasources in `:core:database` (`ARCHITECTURE.md` §6, Local layer); mapper, repository and use case in the consuming feature (`feature.<name>.data` and `.domain`); models used by several features in `:core:shared-domain-model`.

## When to use

- A new endpoint, or a new or renamed field on a request or response
- Writing or reviewing a datasource or repository

## When NOT to use

- UI work on data the app already has: use `create-compose-screen`

## Input

The endpoint (method and path), what the caller needs, and which screen uses it. The real API is the source of truth, not memory.

## Steps

### 1. Probe the real API (read-only)

- Read the official page, then call the endpoint with `curl`. **URL-encode** every term (a raw non-ASCII term returns `400 Bad Request` as HTML).
- Respect the rate limit (iTunes: about 20 calls per minute): a few seconds between calls, and save each response to a file once.
- Capture **several hundred real items** with varied queries. For each field the app will read, count the items where it is absent, `null` or empty. That count decides nullability.
- Test every parameter you plan to rely on, including ones you only assume work (iTunes `offset` is ignored, so the app pages locally).
- Record authentication, limits, what an empty result and an unknown id return, the error body shapes and the content type. Keep a few responses as test fixtures.

### 2. DTOs (`model/`, one file per class)

Follow `ARCHITECTURE.md` §6 (DTOs). Both annotations, app vocabulary with the wire key kept, no defaults, nullable only where step 1 showed absence.

```kotlin
@Serializable
@SerialName("track")
data class ApiTrack(
    @SerialName("trackId") val id: Long,
    @SerialName("trackName") val name: String,
    @SerialName("artworkUrl100") val artUrl: String?,
) : ApiItem
```

A list that mixes kinds uses a sealed interface plus the `Json` configuration below, never a hand-written serializer.

### 3. Json configuration (`api/ItunesJson.kt`)

```kotlin
internal val ItunesJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    classDiscriminator = "wrapperType"
    serializersModule = SerializersModule {
        polymorphicDefaultDeserializer(ApiItem::class) { ApiUnknownItem.serializer() }
    }
}
```

One instance for every response, with a comment saying why each option exists.

### 4. Retrofit service (`api/`)

```kotlin
@GET("search?media=music&entity=song")
suspend fun searchTracksBySearchText(
    @Query("term") searchText: String,
    @Query("limit") limit: Int,
): ApiResponse
```

Methods are named after the endpoint; parameters are named by meaning with the wire key in `@Query`; fixed query parameters live in the `@GET`; every other parameter is explicit (Retrofit ignores Kotlin defaults).

### 5. Datasource

A concrete class per context, no interface, returning the raw response (`ARCHITECTURE.md` §6):

```kotlin
suspend fun getTrack(trackId: Long): Result<ApiResponse> =
    remoteCall { api.lookupTrack(trackId) }
```

- Method names are an action plus what the caller wants (§3, §8). No filtering, no dispatcher switch.
- Local datasource (`:core:database/datasource`): a concrete public class with an `internal` constructor over the DAOs. Suspend calls go through `localCall { ... }`; a stream is `flow { emitAll(dao.observeX()) }.asResult()` (built lazily, because Room checks the open database when the DAO builds its Flow). A write that touches several tables is one `database.withWriteTransaction { ... }`. The caller passes timestamps in.
- Entities are `Db*` with the DTO's property names and snake_case table names; DAOs are `internal` and use `@Upsert`, not a replace, so a cascade never deletes related rows; register new entities in the single `AppDatabase` and bump `version` with a migration when the schema changes.

### 6. Extensions (`extension/`)

Helpers that read a response (`ApiResponse.tracks()`, `album()`) are public because every feature's repository uses them; plumbing (`remoteCall`, `toRemoteError`) is `internal`. File names follow §6.

### 7. Mapper and repository (feature `data/`)

- Mapper: pure extension functions, `fun ApiTrack.toDomain(): TrackDomainModel`, in `data/mapper/`.
- Repository interface in the feature's `domain/repository`; implementation in `data/repository/`:

```kotlin
override suspend fun searchTracks(searchText: String): Result<List<TrackDomainModel>> =
    trackRemoteDataSource.searchTracksBySearchText(searchText, MAX_RESULTS)
        .map { response -> response.tracks().map { it.toDomain() } }
```

- The repository injects `DispatcherProvider` and uses `withContext(dispatchers.io)` only for blocking or CPU-heavy work. It uses `.map` and `fold`, never `runCatching` or `mapCatching`.

### 8. Domain model and use case

- `<Name>DomainModel` in `:core:shared-domain-model` when several features use it, otherwise in the feature's `domain/model`.
- Use case in `domain/usecase/<Verb><Noun>UseCase.kt`: one public method, `suspend operator fun invoke(...): Result<T>` or `operator fun invoke(...): Flow<Result<T>>`.

### 9. Tests

Follow `ARCHITECTURE.md` §9. For a remote datasource: MockWebServer with the fixtures from step 1; assert the request sent, the mapping of a real response, an empty result, and each error mapping.

### 10. Review and report

Run the `clean-code-review` skill on the files you wrote. Report the files changed per layer and what the real-data scan showed.

## Hard constraints

- `CancellationException` is never swallowed; a Flow never throws to its collector
- A datasource has no interface, returns the raw response, and does not switch dispatchers
- An empty result is a successful empty list, never an error
- Repositories never call the Retrofit interface or a DAO directly, and never use `runCatching`
- A failing repository method returns `Result<T>` (`Result<Unit>` for void), never `T?`
- A feature never imports another feature; shared types come from `core:*`
