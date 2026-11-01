---
name: add-data-endpoint
description: Wire a backend endpoint or field through the app's data layers (Api model, Service, datasource, mapper, repository, use case, tests) and prove the JSON matches the API. Use when a new or changed API response/request must reach the app, or when writing or auditing datasource/repository suspend code.
---

Take an endpoint or field to the UI through the layers in `ARCHITECTURE.md` §5 and §6. This skill is the procedure and the coroutine-safety checklist. Ownership is split by module: `Api*`, the Retrofit service and the remote datasource live in `:core:network`; `Db*` entities and DAOs in `:core:database`; datasource, mapper, repository and use case in the consuming feature module (`feature.<name>.data` / `.domain`); models shared by several features in `:core:shared-domain-model`.

## When to use

- A new endpoint, or a new/renamed field on a request or response
- Writing or reviewing a datasource or repository `suspend` function

## When NOT to use

- UI work on data the app already has: use `create-compose-screen`

## Input

The endpoint (method + path), request and response JSON, and which screen needs it. Use the API specification you were given as the source of truth; if there is none, ask for it before writing code.

## Prerequisites

Networking already exists in `:core:network` (Retrofit, OkHttp, kotlinx.serialization, MockWebServer for tests); a new endpoint reuses it. If a dependency is missing, add to `gradle/libs.versions.toml` and the owning module's `build.gradle.kts` (`:core:network` for Retrofit/OkHttp, `:core:database` for Room, the feature module for test libraries): Retrofit, a JSON converter (Gson, Moshi or kotlinx.serialization), OkHttp, `kotlinx-coroutines-core`, and for tests MockK and `kotlinx-coroutines-test`. Add the `INTERNET` permission to the `:app` `AndroidManifest.xml`. Pick the converter once and record it in `ARCHITECTURE.md` §1. Until a DI library is added, build the object graph by hand in one place.

When the source is a local database, also add Room (runtime, ktx, compiler via KSP), and Turbine for Flow tests.

## Suspend or Flow?

Decide before writing code (see `ARCHITECTURE.md` §2). One-shot request or read/write: `suspend ...: Result<T>`. Anything observed over time, typically a Room query: `fun ...: Flow<Result<T>>`. When unsure, use `suspend`. The steps below show the suspend form first and the Flow variant where it differs.

## Steps

### 1. Read the contract

From the API specification, copy the exact JSON keys. Note which fields are optional or defaulted: a wrong key on those is often accepted silently.

### 2. Api models (`:core:network`, `core/network/dto/Api<Name>.kt`)

```kotlin
data class ApiStreak(
    val count: Int,
)
```

Prefix `Api`. Keep the property names or serialization annotations matching the JSON keys exactly.

Use the simplest thing that parses the JSON:

- **Fields renamed:** `@SerialName("json_key")` on the property. (`@SerializedName` is Gson's annotation; with kotlinx.serialization it does nothing.)
- **Unknown or extra keys:** set `ignoreUnknownKeys = true` on the `Json` instance. Do not model fields the app does not use.
- **Fields that may be absent:** make them nullable and set `explicitNulls = false` on the `Json` instance. A missing key then reads as `null`, so the DTO needs no `= null` default. Give a non-null property no default either, so a malformed response fails loudly instead of looking empty. Decide nullability from real data, not caution: scan a few hundred real items, make a field non-null when it is always present, and nullable only when the docs or the data show it can be absent.
- **A list that mixes kinds** (for example a lookup that returns an album first and then its tracks, told apart by a `wrapperType` field): do **not** write a custom `KSerializer`. Declare a `@Serializable sealed interface`, give each subtype `@SerialName("<discriminator value>")`, and configure the `Json` instance once:

```kotlin
val ApiJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "wrapperType"
    serializersModule = SerializersModule {
        polymorphicDefaultDeserializer(ApiItem::class) { ApiUnknownItem.serializer() }
    }
}
```

  The default deserializer maps any kind the app does not use to an `@Serializable data object`, so a new kind from the server never breaks parsing. A hand-written serializer is only justified when the discriminator is not a plain string field.

### 3. Service (`:core:network`, `core/network/api/Service.kt`)

A Retrofit interface with a `suspend` function returning the `Api*` model.

For a Room source instead: a `Db<Name>` entity and a DAO in `:core:database` (`core/database/{entity,dao}`; register the entity in the single `AppDatabase`) whose observed read returns `Flow<List<Db<Name>>>`, while single reads (get by id) and all writes (insert, update, delete) are `suspend`. Entities map to domain exactly like `Api*` models.

### 4. DataSource

Remote datasources live in `:core:network`, split by context (`TrackRemoteDataSource`, `AlbumRemoteDataSource`); a feature's `data/datasource/<Context>LocalDataSource.kt` wraps a DAO. A datasource is a **concrete class with no interface**, injected by constructor: the Retrofit service and the DAO are already the abstractions, and the replaceable boundary is the repository interface in the feature's `domain`.

Name datasource methods as an action plus what the caller wants, in the app's words: `getTrack(id)` (one, by id), `searchTracksBySearchText(searchText, limit)` (many, by text), `getAlbumWithTracks(id)` (says what comes back). Name the Retrofit service methods after the endpoint instead (`lookupTrack`, `searchTracksBySearchText`). Put the shared `runCatching` plumbing in one extension or helper (`remoteCall { ... }`) instead of repeating it:

```kotlin
suspend fun getExercises(lessonId: String): Result<List<ApiExercise>> =
    remoteCall { api.getExercises(lessonId) }
```

where the helper is:

```kotlin
internal suspend fun <T> remoteCall(request: suspend () -> T): Result<T> =
    runCatching { request() }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it.toRemoteError()) }, // rethrows CancellationException
    )
```

Flow variant (`runCatching` cannot see errors in later emissions, so use `.catch`):

```kotlin
fun observeExercises(lessonId: String): Flow<Result<List<DbExercise>>> =
    dao.observeByLesson(lessonId)
        .map { Result.success(it) }
        .catch { if (it is CancellationException) throw it else emit(Result.failure(it)) }
```

A Room one-shot (read by id, or a write) uses the suspend form with the DAO in place of the API, e.g. `runCatching { dao.upsert(exercise) }` returning `Result<Unit>`. For writes, the repository maps domain to `Db*` (`toDb()`) before calling the datasource.

A datasource does not switch dispatchers: Retrofit and Room suspend and `Flow` calls are main-safe. The repository injects a `DispatcherProvider` and switches only when it does blocking or CPU-heavy work. A datasource returns the raw response or entities as they are, with no filtering, `firstOrNull()` or invented aggregate types: reading a mixed response is the repository mapper's job (shared extensions such as `ApiResponse.tracks()` live in `:core:network` as public extensions). Any `runCatching`, `catch (e: Exception)` or Flow `.catch` must rethrow `CancellationException` first.

### 5. Mapper (feature `data/mapper/Api<Name>Mapper.kt`)

```kotlin
fun ApiStreak.toDomain(): Int = count
```

A pure extension function with no injected dependencies.

### 6. Domain and repository

- Domain model `<Name>DomainModel` in `:core:shared-domain-model` when several features use it, otherwise in the feature's `domain/model` (pure Kotlin)
- The repository interface in the feature's `domain/repository`
- Implementation in the feature's `data/repository/<Domain>RepositoryImpl.kt`:

```kotlin
override suspend fun getExercises(lessonId: String): Result<List<ExerciseDomainModel>> =
    exerciseApiDataSource.getExercises(lessonId).map { dtos -> dtos.map { it.toDomain() } }
```

Flow variant: map each emission's `Result`, so the mapping stays in the repository.

```kotlin
override fun observeExercises(lessonId: String): Flow<Result<List<ExerciseDomainModel>>> =
    exerciseLocalDataSource.observeExercises(lessonId)
        .map { result -> result.map { entities -> entities.map { it.toDomain() } } }
```

Repositories use `.map`, never `runCatching` or `mapCatching` (`mapCatching` swallows cancellation). To turn a failure into a domain error, use `fold`.

### 7. Use case (feature `domain/usecase/<Verb><Noun>UseCase.kt`)

One public method, verb name; `Get` never persists. `suspend operator fun invoke(...): Result<T>` for one-shot calls, `operator fun invoke(...): Flow<Result<T>>` for streams. The ViewModel maps domain to `Ui*`; nothing here does.

### 8. Tests

- Use case and repository tests with MockK
- Flow tests with Turbine: assert a success emission, a `Result.failure` emission (the Flow completes without throwing), and that cancellation is not turned into a `Result.failure`
- A serialization test that pins the JSON for requests, asserting the exact JSON string with the same converter configuration the app uses (the `:core:network` test folder for DTOs)

### 9. Prove the contract against the API

Unit tests mock the network, so they cannot show that the keys match. Call the real endpoint once (staging or a local server) with the real Retrofit `Service`, e.g. from a JVM test gated on an environment variable so it skips when unset, and check the result matches the specification. Report this as done or blocked.

### 10. Clean code check

Re-read what you wrote against `ARCHITECTURE.md` §8:

- Names reveal intent and follow one vocabulary: datasources and repositories `get`/`search`/`save`/`observe` plus the thing (`getTrack`, `searchTracksBySearchText`), the Retrofit service named after the endpoint, use cases `<Verb><Noun>`. No `Manager`, `Helper` or `Util`.
- Each function does one thing at one level of abstraction: request, mapping and error handling are separate functions.
- No magic strings: fixed query parameters live in the Retrofit annotation (`@GET("search?media=music&entity=song")`) or a named constant.
- No flag or nullable-as-default parameters: two functions beat one with a switch.
- Mappers are pure and one-purpose; DTOs carry only the fields the app uses.
- No dead code: an unused field, function or error type is deleted.
- Tests: one concept each, named as sentences, no logic, no network.

### 11. Report

Files changed per layer, any audit finding (a swallowed `CancellationException`, a `Result<T>` function that also throws), and whether step 9 was run.

## Hard constraints

- `CancellationException` is never swallowed
- Datasources catch; repositories and use cases propagate `Result<T>` (`Flow<Result<T>>` for streams); ViewModels use `.onSuccess`/`.onFailure`
- A Flow never throws to its collector: failures are emitted as `Result.failure`
- `Flow` only for real streams; one-shot calls stay `suspend`
- Repositories never call the Retrofit interface or a DAO directly
- A datasource has no interface and does not switch dispatchers; the repository owns the injected `DispatcherProvider`
- A feature never imports another feature's data or domain; shared types come from `core:*`
- A failing repository method returns `Result<T>` (`Result<Unit>` for void), never `T?`, and never both returns and throws
