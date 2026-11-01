# Moisesai Challange — Architecture Reference

## 1. Current state

- Today: `:app`, `:core:common`, `:core:design`, `:core:network` (Retrofit, OkHttp, kotlinx.serialization, `TrackRemoteDataSource`, `AlbumRemoteDataSource`), plus empty `:core:shared-domain-model` (JVM-only), `:core:database`, `:core:player`, and empty `:feature:songs`, `:feature:songdetails`, `:feature:album` (feature convention plugin: Compose, Hilt/KSP, `:core:design`, `:core:common`, `:core:shared-domain-model`). Package `com.moisesai.challange`.
- Present: `MainActivity`; `:core:common` (`AppError`, dispatchers); `:core:design` (theme in `core/design/.../theme/{Color,Theme,Type}.kt`, `@FullPreview` in `.../preview/FullPreview.kt`); build-logic convention plugins; Compose, Material 3.
- Hilt: configured in `:app` (`@HiltAndroidApp` `MoisesaiChallangeApplication`, `@AndroidEntryPoint` `MainActivity`), KSP for the compiler; no bindings yet.
- Not present yet: database (Room), ViewModels, navigation, domain/data code, and the module split below.
- Target: modules **by feature** (section 2). New code follows that layout.
- Versions and SDKs: `gradle/libs.versions.toml` and `app/build.gradle.kts`. JDK per `gradle/gradle-daemon-jvm.properties`.

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

- `feature:*` depends on `core:*`. Features never depend on or import each other. `:app` connects them with type-safe routes and lambdas (ids only: Player receives a track id, Album a collection id).
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

- `Api*` DTOs, the Retrofit interface and the remote datasources (`TrackRemoteDataSource`, `AlbumRemoteDataSource`, split by context) live in `:core:network`.
- **Datasources are concrete classes with no interface**, injected by constructor. The Retrofit service and the DAO are already the abstractions they wrap, and the boundary that keeps the API replaceable is the repository interface in the feature's `domain`. Replacing the API means rewriting the datasources, the `Api*` models and the repository mappers; the domain, the ViewModels and the UI stay untouched. Tests fake the layer below (MockWebServer, an in-memory Room) or mock the concrete class with MockK.
- A datasource wraps one source for one context and returns the raw `Api*` / `Db*` models in `Result<T>` (`Flow<Result<T>>` for streams). It does no filtering or reshaping: a remote datasource returns the whole response (`Result<ApiResponse>`), and the repository's mapper reads it, using the shared `ApiResponse.tracks()` / `album()` extensions from `:core:network` for a response that mixes kinds. Method names follow the vocabulary in section 8: an action plus what the caller wants (`searchTracksBySearchText`, `getTrack`, `getAlbumWithTracks`).
- **Datasources do not switch dispatchers.** Retrofit suspend functions and Room suspend and `Flow` queries are main-safe. The repository injects a `DispatcherProvider` and switches (`withContext(dispatchers.io)`) only when it does blocking or CPU-heavy work, such as mapping a large list.
- `runCatching` lives in suspend datasources only, and rethrows `CancellationException`:

```kotlin
runCatching { api.getExercises(lessonId) }
    .onFailure { if (it is CancellationException) throw it }
```

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
- **One main concept per file**, named after it. A tiny sealed hierarchy or a group of DTOs that only make sense together may share a file.
- **Newspaper order.** The most important declaration first, details below.
- **Depend on abstractions** and hide implementations: `internal` or `private` by default, public only for what other modules need.
- **Organize by feature**, then by layer (section 2), never by technical kind alone.

### Variables and state

- **`val` over `var`**, immutable data classes, no shared mutable state.
- **Declare close to first use**, with the narrowest scope.
- **No magic numbers or strings.** A named constant, or a named argument that explains it. Prefer a typed value (enum, sealed class, value class) over a raw string.
- **Explanatory variables.** Name an intermediate result when it makes a long expression readable.
- **Null is a decision.** Nullable only where absence is a real state (an optional API field). Never pass `null` to mean "default".

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
- A test that only restates a constant or a getter is deleted.

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

- Unit tests: JUnit 4, MockK, `kotlinx-coroutines-test` and Turbine come from the `moisesai.android.library` convention plugin, so every Android module has them. MockWebServer (`mockwebserver3`) is added in `:core:network` only. `:core:shared-domain-model` (JVM) has JUnit only. ViewModels, use cases and repositories are always tested.
- Tests live in the module that owns the code: network with MockWebServer in `:core:network`, in-memory Room in `:core:database`, use cases, repositories and ViewModels in their feature module.
- An architecture test (Konsist or ArchUnit) fails when presentation imports data, domain imports Android, a feature imports another feature, or `core:*` imports a feature.
- Commands: `./gradlew test`, `./gradlew lint`, `./gradlew connectedAndroidTest`. Gradle wrapper only.
- No CI and no static analysis configured yet.
