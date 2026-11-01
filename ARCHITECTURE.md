# Moisesai Challange — Architecture Reference

**This file is the single source of the project's rules.** `CLAUDE.md` holds one-line versions that point here, and the skills hold procedures and examples. To change a rule, edit it here first, then update its one-line version in `CLAUDE.md` and any example in a skill, and search the repository for the old wording.

## 1. Current state

- **Modules:** `:app`, `:core:common`, `:core:design` and `:core:network` have code. Empty so far: `:core:shared-domain-model` (JVM-only), `:core:database`, `:core:player`, `:feature:songs`, `:feature:songdetails`, `:feature:album`. Package `com.moisesai.challange`.
- **Done:** `build-logic` convention plugins, Hilt in `:app`, test tooling, the theme and `@FullPreview` in `:core:design`, `AppError` and `DispatcherProvider` in `:core:common` (the provider is for repositories and has no user yet), and the remote layer in `:core:network` (section 6).
- **Not present yet:** Room, repositories, use cases, ViewModels, navigation and the real screens. `MainActivity` shows a `TemporaryNetworkCheck` (section 11).
- **Versions and SDKs:** `gradle/libs.versions.toml` and `app/build.gradle.kts`. JDK per `gradle/gradle-daemon-jvm.properties`.

Everything below the next heading is the convention for code **as it is added**. When a section needs a dependency that is missing, add it to `gradle/libs.versions.toml` first.

---

## 2. Layers

```text
Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API
```

- **Presentation -> Domain:** ViewModels never call the API or DataSources. They go through use cases.
- **Domain -> Data:** domain knows only repository interfaces; implementations live in `data`.
- **Modules by feature, layers as packages:** there is no global `:domain` or `:data` module. Each feature module contains its own presentation, domain and data packages.

### Modules

```text
:app                       Application, MainActivity, NavHost (the only place features meet), DI root, splash
:feature:songs             search, pagination, recents list, More-options sheet
:feature:songdetails       song details screen (player): play/pause, seek slider, timeline, skip (uses :core:player)
:feature:album             album header and track list
:core:design               theme, @FullPreview, shared components
:core:common               AppError, dispatchers, small extensions
:core:shared-domain-model  pure Kotlin shared domain models only (TrackDomainModel, AlbumDomainModel): no logic, no Android
:core:network              OkHttp/Retrofit setup, ItunesApi, Api* DTOs, remote datasources (track, album)
:core:database             Room AppDatabase, Db* entities, DAOs
:core:player               playback engine: PlayerController interface + Media3 ExoPlayer implementation (knows no feature or iTunes code)
```

### Feature package layout

```text
com.moisesai.challange.feature.<name>
├── presentation/   composable/  model/  view_model/  navigation/  mapper/
├── domain/         usecase/  repository/ (interfaces)   # pure Kotlin, no Android imports
└── data/           datasource/  repository/  mapper/
```

Core packages: `com.moisesai.challange.core.{design,common,shareddomainmodel,network,database}`.

### Dependency rules

- `feature:*` depends on `core:*`. Features never depend on or import each other. `:app` connects them with type-safe routes and lambdas (ids only: Player receives a track id, Album an album id).
- `core:*` never depends on a feature. `core:shared-domain-model` depends on nothing. `:core:player` knows nothing about tracks or iTunes: it plays a URL.
- Inside a feature: `presentation -> domain <- data`. Presentation never imports data.
- State shared by several features goes through `core:*` (a shared model, or a Room table behind a DAO); each feature keeps its own repository interface and use cases over it. Songs reads recents and Player writes them through the same DAO, not through each other.
- Layers are packages, so Gradle does not enforce the third rule: an architecture test (Konsist or ArchUnit) does (section 9).
- Why `core:*` exists: tracks, the iTunes client and the Room database are used by all three features, and Room needs a single `AppDatabase` that knows every entity. Keep it to infrastructure and shared models; no feature logic.

### Suspend or Flow?

`suspend` is the default. `Flow` is for streams only: a Room query observed over time, or any source that emits more than once. Room one-shots are `suspend`: a read of a single result (get by id, "load once") and every write (insert, update, delete). The same table is queried either way: choose by whether the caller wants the current value once or every future change. A single value is never wrapped in a `Flow`: callers cannot tell how many emissions to expect, and every collector re-runs the work.

| Layer | Suspend (one-shot) | Flow (stream) | Mapping owned here |
|---|---|---|---|
| DataSource | `suspend fun`: `Result<Api*>` / `Result<Db*>` | `fun`: `Flow<Result<Api*/Db*>>` | none, returns raw models |
| Repository | `suspend fun`: `Result<*DomainModel>` | `fun`: `Flow<Result<*DomainModel>>` | **data -> domain** |
| UseCase | `suspend operator fun invoke` | `operator fun invoke` | none (business rules only) |
| ViewModel | `viewModelScope.launch { useCase()... }` | `viewModelScope.launch { useCase().collect { ... } }` | **domain -> `Ui*`** |
| Composable | receives `UiState` | receives `UiState` | none |

The Flow stops at the ViewModel: it is collected there and written into the single `StateFlow<UiState>`.

---

## 3. Naming by layer

| Layer | Convention | Example |
|---|---|---|
| Presentation | prefix `Ui` | `UiExercise` |
| Domain | suffix `DomainModel` | `ExerciseDomainModel` |
| Data / API | prefix `Api` | `ApiExercise` |
| Data / Room | prefix `Db` | `DbExercise` |

Package names are singular, lowercase, no underscores for modules and layers (`feature.songs.presentation`). Presentation subpackages use the names in section 2 (`view_model` with underscore is the one exception).

### Vocabulary

One word for one thing, in every layer:

- **`track` and `album`**, never `song` or `collection` (iTunes calls an album a "collection" and a track a "song" in places; our code does not).
- **`id` and `name`** are the model's own identity. A reference to another model is prefixed: `albumId`, `albumName`.
- **Short, plain property names:** `artUrl`, not `artworkUrl100`; `artistName`, not `artistNameText`.
- **Names are ours, wire names stay on the wire.** A DTO property uses the app's word and keeps the JSON key with `@SerialName("collectionId") val id: Long`. A Retrofit parameter is named by meaning (`searchText`) and keeps the wire key in `@Query("term")`.
- **Methods are an action plus what the caller wants** (section 8): `searchTracksBySearchText`, `getTrack`, `getAlbumWithTracks`.

---

## 4. Presentation

- `@HiltViewModel` with `@Inject constructor`, obtained with `hiltViewModel()` (needs `androidx.hilt:hilt-navigation-compose`, added with the first screen).
- State: `StateFlow<UiState>` (`_uiState` private, `uiState` exposed), consumed with `collectAsStateWithLifecycle()` in the navigation graph, not in the screen composable.
- `data class UiState` when states overlap; a `sealed class` when they are mutually exclusive. `isLoading` and `error` exist on every `UiState`.
- ViewModels hold UI logic only (loading/error state, domain -> UI mapping). Business rules belong in use cases.
- A suspend use case is called once inside `viewModelScope.launch`. A `Flow<Result<T>>` use case is collected inside `viewModelScope.launch` (or `stateIn` when the state is purely derived from the stream). Either way each `Result` is folded into `UiState` with `.onSuccess`/`.onFailure`, and domain models are mapped to `Ui*` there. Composables never collect a domain Flow.
- Navigation is driven by lambdas passed in from the root `NavHost`; ViewModels and feature composables never own the `NavController`.
- Screen composables are stateless: `uiState` plus callbacks. `modifier: Modifier = Modifier` is the first optional parameter.
- Top-level screen composables have `@FullPreview` previews wrapped in `MoisesaiChallangeTheme { }`.

## 5. Domain

- Pure Kotlin, no Android imports. Models end in `DomainModel`.
- One public method per use case; verb names (`GetExercisesUseCase`); `Get` never persists, use `Save`/`Submit`.
- `suspend operator fun invoke(...): Result<T>` for one-shot calls, `operator fun invoke(...): Flow<Result<T>>` for streams (see "Suspend or Flow?" in §2).
- Repository interfaces live in the feature's `domain/repository`; use cases in its `domain/usecase`. Models shared by several features live in `:core:shared-domain-model`; a model used by one feature stays in that feature's `domain`.

## 6. Data

### Remote layer (`:core:network`)

```text
core/network
├── api/         ItunesApi (the Retrofit service), ItunesApiFactory, ItunesJson
├── model/       Api* DTOs, one file per class
├── extension/   extension functions: ApiResponseExtensions, RemoteCallExtension, RemoteErrorExtension
├── datasource/  concrete remote datasources, one per context (TrackRemoteDataSource, AlbumRemoteDataSource)
└── di/          Hilt modules
```

**Service (`ItunesApi`)**
- Methods are named after the endpoint they call (`lookupTrack`, `lookupAlbumWithTracks`, `searchTracksBySearchText`).
- Fixed query parameters live in the `@GET` annotation (`@GET("search?media=music&entity=song")`), not in code. Every other parameter is explicit, because Retrofit does not apply Kotlin default arguments.

**DTOs (`model/`)**
- One file per class, prefix `Api`, `@Serializable` **and** `@SerialName`. They do different jobs: `@Serializable` generates the serializer (leave it off and the code still compiles but fails at runtime), and `@SerialName` gives the name the JSON uses (the discriminator value on a polymorphic subtype, or the key of a renamed property).
- Property names follow the vocabulary in section 3, with `@SerialName("jsonKey")` keeping the wire key.
- **Nullability comes from real data, not caution.** Scan a few hundred real items: a field that is always present is non-null; a field is nullable only when the docs or the data show it can be absent. A non-null field missing from the response fails the whole response as `AppError.Unknown`; that loud failure is intended.
- **No default values.** `explicitNulls = false` on the `Json` instance already reads a missing nullable field as `null`, and a non-null property with no default fails loudly instead of hiding a malformed response.
- A list that mixes kinds (a lookup returns the album first, then its tracks) uses a `@Serializable sealed interface` with the discriminator and a default for kinds the app does not use. No hand-written `KSerializer`.
- Model only the fields the app uses; `ignoreUnknownKeys = true` skips the rest.

**Datasources**
- **Concrete classes with no interface**, injected by constructor, split by context. The Retrofit service and the DAO are already the abstractions they wrap, and the boundary that keeps the API replaceable is the repository interface in the feature's `domain`. Replacing the API means rewriting the datasources, the `Api*` models and the repository mappers; the domain, the ViewModels and the UI stay untouched. Tests fake the layer below (MockWebServer, an in-memory Room) or mock the concrete class with MockK.
- A datasource wraps one source for one context and returns the **raw** response in `Result<T>` (`Flow<Result<T>>` for streams): `Result<ApiResponse>` for a remote call. It does no filtering, `firstOrNull()` or reshaping, and invents no aggregate type. The repository's mapper reads the response with the shared public extensions `ApiResponse.tracks()` and `album()`.
- **Datasources do not switch dispatchers.** Retrofit suspend functions and Room suspend and `Flow` queries are main-safe. The repository injects a `DispatcherProvider` and switches (`withContext(dispatchers.io)`) only when it does blocking or CPU-heavy work, such as mapping a large list.
- The shared error handling is one helper, `remoteCall { ... }`, not repeated in each method:

```kotlin
suspend fun getTrack(trackId: Long): Result<ApiResponse> =
    remoteCall { api.lookupTrack(trackId) }
```

**Errors**
- `AppError` has two cases: `Network` (no connection, a dropped connection, or any non-2xx HTTP status) and `Unknown` (a body that does not parse, or a required field that is missing). Cancellation is rethrown, never turned into a failure.
- **An empty result is a success with an empty list**, not an error. iTunes answers an empty search and an unknown id with HTTP 200 and no results, so there is no "empty" error.

**Extensions (`extension/`)**
- Files are named `<Subject>Extensions.kt` when they hold several functions (`ApiResponseExtensions.kt`) and `<Subject>Extension.kt` when they hold one (`RemoteCallExtension.kt`, `RemoteErrorExtension.kt`).
- `internal` unless another module needs it. `tracks()` and `album()` are public because every feature's repository uses them; `remoteCall` and `toRemoteError` are internal.

### Streams, repositories, mappers and Room

- `runCatching` lives in suspend datasources only (inside `remoteCall` for the remote ones), and rethrows `CancellationException`.
- Stream datasources cannot use `runCatching`, because it does not see errors thrown during later emissions. They use `.catch`, which is the Flow form of the same rule, and the cancellation guard is mandatory:

```kotlin
fun observeExercises(lessonId: String): Flow<Result<List<DbExercise>>> =
    dao.observeByLesson(lessonId)
        .map { Result.success(it) }
        .catch { if (it is CancellationException) throw it else emit(Result.failure(it)) }
```

  A Flow never throws to its collector: every failure is an emitted `Result.failure`.
- Repositories depend on datasources, never on the API interface or a DAO. They map with `.map { it.toDomain() }` for suspend, and `.map { result -> result.map { it.toDomain() } }` for Flow. They never use `runCatching` or `mapCatching`.
- Mappers are pure extension functions: `fun ApiExercise.toDomain()` / `fun DbExercise.toDomain(): ExerciseDomainModel` (called from repositories) and `fun ExerciseDomainModel.toUiModel(): UiExercise` (called from ViewModels, never from composables). A domain model never carries an `Api*` or `Db*` field.
- Room entities and DAOs live in `:core:database` (`core/database/{entity,dao}`), because a single `AppDatabase` must know every entity. A DAO may return `Flow<List<Db*>>` directly, because only datasources touch it. DAO one-shot reads and writes are `suspend` (Room runs them off the main thread itself), and the local datasource wraps them like an API call:

```kotlin
suspend fun saveExercise(exercise: DbExercise): Result<Unit> =
    runCatching { dao.upsert(exercise) }
        .onFailure { if (it is CancellationException) throw it }
```

  Writes return `Result<Unit>`; the repository maps the domain model to `Db*` on the way in (`ExerciseDomainModel.toDb()`) and `Db*` to domain on the way out.
- Failures reach the UI as `Result.failure` and become the `error` field of `UiState`.

### Error handling by layer
DataSources catch (`runCatching` for suspend, `.catch` for Flow). Repositories and use cases propagate `Result<T>` or `Flow<Result<T>>`. ViewModels use `.onSuccess`/`.onFailure`, per emission for a stream. Exceptions are never used for flow control.

---

## 7. Theming

Screens take colours, typography and shapes from `MaterialTheme.colorScheme`, `MaterialTheme.typography` and `MaterialTheme.shapes`. Change the palette in `core/design/theme/Color.kt` and `Theme.kt`, not at call sites. The theme uses dynamic colour on Android 12+ and follows the system dark mode. Never hardcode hex colours, corner radii or font sizes.

---

## 8. Code quality (Clean Code)

Robert C. Martin's *Clean Code* applies to every file, Kotlin idioms included. When a rule below conflicts with a convention elsewhere in this document, the architecture convention wins. The Boy Scout rule applies: leave the file cleaner than you found it.

### Names

- **Reveal intent.** A name answers why it exists, what it does and how it is used. If it needs a comment, rename it. `elapsedMs` beats `t`; `activeTracks` beats `list2`.
- **Classes and types are nouns** (`TrackRepository`, `AppError`); **functions are verbs** (`getAlbumWithTracks`, `toAppError`); **booleans read as questions** (`isLoading`, `hasPreview`, `canSeek`).
- **No noise or disinformation.** No `Manager`, `Helper`, `Util`, `Handler`, `Data`, `Info`; no `List` in the name of something that is not a `List`; no type prefixes or encodings (`IFoo`, `strName`). Layer markers (`Ui*`, `*DomainModel`, `Api*`, `Db*`) are a project convention, not an encoding.
- **One word per concept.** Pick one verb for one idea across the code base and keep it: `get` reads one thing by id, `search` finds many by text, `save` writes, `observe` streams. A layer speaks its own level: datasources and repositories use the app's words (`searchTracksBySearchText`, `getAlbumWithTracks`), the Retrofit service names methods after the endpoint (`lookupTrack`).
- **Pronounceable and searchable.** No abbreviations beyond the well-known (`id`, `url`, `ui`). Name constants, so they can be found.
- **Length follows scope.** A loop variable may be `i` or `it`; a public API gets a full name.

### Functions

- **Small, and they do one thing.** If a function needs a section comment, or "and" in its name, split it.
- **One level of abstraction per function.** Do not mix business rules with parsing or error plumbing. Extract the lower level and name it.
- **Read top to bottom** (the stepdown rule): a public function first, the helpers it calls below it.
- **Few arguments.** Zero to three. More than three, or arguments that always travel together, become a parameter object.
- **No flag arguments** (`render(true)`): write two functions, or use an enum or sealed type.
- **No hidden side effects.** A function either does something (command) or answers something (query), not both. `Get` never persists.
- **Fail with a result, not a code.** Return `Result<T>`; never return `null` or a magic value to mean "failed". Extract the `try`/`runCatching` body into its own function so the error handling is separate from the logic.
- **Do not repeat yourself.** Extract duplicated logic on the third occurrence, not the second.

### Classes and files

- **Small, with a single responsibility** and one reason to change. A class that you can only describe with "and" does two jobs.
- **High cohesion.** Members use the class's state; unrelated helpers move out.
- **One class per file**, named after it: every DTO has its own file. Only a small sealed hierarchy that cannot be read apart may share one.
- **Packages group by kind inside a module** (`api`, `model`, `extension`, `datasource`, `di`), and extension files are named `<Subject>Extensions.kt` (several functions) or `<Subject>Extension.kt` (one function).
- **Newspaper order.** The most important declaration first, details below.
- **Depend on abstractions** and hide implementations: `internal` or `private` by default, public only for what other modules need.
- **Organize by feature**, then by layer (section 2), never by technical kind alone.

### Variables and state

- **`val` over `var`**, immutable data classes, no shared mutable state.
- **Declare close to first use**, with the narrowest scope.
- **No magic numbers or strings.** A named constant, or a named argument that explains it. Prefer a typed value (enum, sealed class, value class) over a raw string.
- **Explanatory variables.** Name an intermediate result when it makes a long expression readable.
- **Null is a decision.** Nullable only where absence is a real state, and for an API field that means real data or the docs show it can be absent (section 6). Never pass `null` to mean "default", and give no property a default value just to avoid thinking about it.

### Comments and formatting

- **Comments explain why** (hidden constraint, workaround, invariant), never what. Good code does not need a *what* comment.
- **No commented-out code, no journal or banner comments, no noise** (`// constructor`). Version control keeps history.
- **KDoc only on public API whose contract is not obvious.**
- **Formatting is the Kotlin official style.** Related code stays close together; blank lines separate concepts, not lines of one idea. No unused imports, parameters or code (delete it; YAGNI).

### Error handling

- Errors are values at layer boundaries (`Result<T>`, `AppError`); coroutine cancellation is never swallowed. Do not catch an exception you cannot handle, and never leave a `catch` block empty.

### Tests (F.I.R.S.T.)

- **Fast, Independent, Repeatable, Self-validating, Timely.** No test depends on another or on the network, the clock or the order of execution.
- **One concept per test**, with a name that reads as a sentence (backticks). Arrange, act and assert are visible.
- **No logic in tests** (no loops or conditionals around assertions). Test code gets the same cleanliness rules as production code.
- A test that only restates a constant or a getter is deleted. So is a test that still passes when the code under test is broken: break the code on purpose and the test must fail (section 9).

### SOLID in this project

| Principle | Where it shows |
|---|---|
| Single responsibility | One use case does one job; a datasource wraps one source; a mapper only maps |
| Open/closed | A new kind of API item is a new subtype, not an edit to a `when` in the parser |
| Liskov substitution | Any implementation of a repository interface (the real one or a fake) behaves the same to its callers |
| Interface segregation | Repository interfaces list only what their callers use; datasources are split by context, so a feature depends only on the ones it uses |
| Dependency inversion | The domain owns repository interfaces; data implements them; the API is replaceable behind the repository interface |

---

## 9. Testing

**Libraries.** JUnit 4, MockK, `kotlinx-coroutines-test` and Turbine come from the `moisesai.android.library` convention plugin, so every Android module has them. MockWebServer (`mockwebserver3`) is added in `:core:network` only. `:core:shared-domain-model` (JVM) has JUnit only. Instrumented tests are enabled only in a module that has `src/androidTest`.

**Where tests live.** In the module that owns the code: remote datasources with MockWebServer in `:core:network`, in-memory Room in `:core:database`, use cases, repositories and ViewModels in their feature module. ViewModels, use cases and repositories are always tested.

**Fixtures come from the real API.** Capture them with read-only calls, store them in `src/test/resources/fixtures/`, and trim them only by removing items from a list. Never invent a response that the API could send. Synthetic JSON is for cases the API will not produce (a malformed body, a missing required field), written inline in the test.

**What a remote datasource test covers:** the request that is sent (path and query), the mapping of a real response, an empty result, and each error mapping (HTTP status, dropped connection, malformed body, missing required field). Pure helpers such as `remoteCall` get their own tests with no network.

**How tests are written:** one concept per test, named as a sentence in backticks; helpers shared by several test classes live in one `<Module>TestSupport.kt` file; no logic in tests.

**Prove the tests can fail.** Break the production code on purpose (for example make the error mapping return `Unknown` for everything), confirm the right tests fail, then restore it. A test that passes either way is deleted or rewritten.

**Architecture test.** Konsist or ArchUnit fails when presentation imports data, domain imports Android, a feature imports another feature, or `core:*` imports a feature.

**Commands.** `./gradlew test`, `./gradlew lint`, `./gradlew connectedAndroidTest`; Gradle wrapper only. The full local check is `./gradlew clean assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest`. No CI and no static analysis configured yet.

---

## 10. External API: iTunes Search (verified)

Verified with live read-only calls on 2026-10-31 and the [official page](https://performance-partners.apple.com/search-api). The sample requests and responses are in `core/network/itunes-api-samples.md` (local, git-ignored).

| Topic | Finding |
|---|---|
| Key | None. Plain HTTPS `GET` on `https://itunes.apple.com/` |
| Rate limit | About 20 calls per minute; the page recommends caching |
| `limit` | 1 to 200, default 50, values above 200 are capped (a search for 200 returned 198 to 200 results) |
| **`offset`** | **Not documented and ignored in every test** (songs, albums, artists, videos, podcasts, ebooks): offsets 0 to 200 returned the same first page. So the app fetches up to 200 results once, caches them, and pages locally. Real paging is an open item |
| Response | Content type `text/javascript`, about 30 fields per item, so the parser ignores the content type and unknown keys |
| Empty or unknown | HTTP 200 with `resultCount: 0`: a success with an empty list |
| Bad request | HTTP 400 with an HTML body, for example when the term is not URL-encoded (Retrofit encodes it) |
| Album lookup | `/lookup?id=<albumId>&entity=song` returns the album (`wrapperType: collection`) first, then its tracks in track order |
| `collection` | Is an album: `collectionType` was `Album` in 17 of 17 lookups, singles and EPs included |
| `trackCount` | The album's track count as iTunes states it; it matched the tracks returned in 16 of 17 lookups |
| Fields | In 3,455 real tracks and 8 albums every field the app reads was present, except `releaseDate` (absent once). The docs say the artwork is returned only when available |
| Artwork | The URL holds the size (`100x100bb`); `600x600bb` and `1000x1000bb` also return an image |

---

## 11. Temporary code

Code added only to check something by hand (a probe, a debug screen, a manual request) follows these rules:

- It lives in **one file** named `Temporary<Something>.kt`, with a KDoc that starts with `TEMPORARY` and says exactly what to delete: the file, its use, and any dependency or manifest line added for it.
- Its use in production files is as small as possible (one call), so removing it is deleting a file and a line.
- It is listed when you hand work over, and it is removed in the step that makes it unnecessary. Today: `TemporaryNetworkCheck.kt` in `:app`, removed with the first real screen.
- It is exempt from the `R.string.*` and `@FullPreview` rules while it exists, and from nothing else.
