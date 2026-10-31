# Moisesai Challange — Architecture Reference

## 1. Current state

- Today: `:app`, `:core:common`, `:core:design`, plus empty `:core:shared-model` (JVM-only), `:core:network`, `:core:database` and `:library:player`. Package `com.moisesai.challange`.
- Present: `MainActivity`; `:core:common` (`AppError`, dispatchers); `:core:design` (theme in `core/design/.../theme/{Color,Theme,Type}.kt`, `@FullPreview` in `.../preview/FullPreview.kt`); build-logic convention plugins; Compose, Material 3.
- Hilt: configured in `:app` (`@HiltAndroidApp` `MoisesaiChallangeApplication`, `@AndroidEntryPoint` `MainActivity`), KSP for the compiler; no bindings yet.
- Not present yet: networking, database (Room), ViewModels, navigation, domain/data code, and the module split below.
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
:app                  Application, MainActivity, NavHost (the only place features meet), DI root, splash
:feature:songs        search, pagination, recents list, More-options sheet
:feature:songdetails  song details screen (player): play/pause, seek slider, timeline, skip (uses :library:player)
:feature:album        album header and track list
:core:design          theme, @FullPreview, shared components
:core:common          AppError, dispatchers, small extensions
:core:shared-model    pure Kotlin shared models only (TrackDomainModel, AlbumDomainModel): no logic, no Android
:core:network         OkHttp/Retrofit setup, ItunesApi, Api* DTOs, remote datasource interface + iTunes implementation
:core:database        Room AppDatabase, Db* entities, DAOs
:library:player       reusable playback engine: PlayerController interface + Media3 ExoPlayer implementation (no feature or iTunes knowledge)
```

### Feature package layout

```text
com.moisesai.challange.feature.<name>
├── presentation/   composable/  model/  view_model/  navigation/  mapper/
├── domain/         usecase/  repository/ (interfaces)   # pure Kotlin, no Android imports
└── data/           datasource/  repository/  mapper/
```

Core packages: `com.moisesai.challange.core.{design,common,sharedmodel,network,database}`.

### Dependency rules

- `feature:*` depends on `core:*` and `library:*`. Features never depend on or import each other. `:app` connects them with type-safe routes and lambdas (ids only: Player receives a track id, Album a collection id).
- `core:*` and `library:*` never depend on a feature. `core:shared-model` depends on nothing. `library:*` modules are generic and reusable: they know nothing about tracks, iTunes or the app's models (`:library:player` plays a URL).
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
- Repository interfaces live in the feature's `domain/repository`; use cases in its `domain/usecase`. Models shared by several features live in `:core:shared-model`; a model used by one feature stays in that feature's `domain`.

## 6. Data

- `Api*` DTOs, the Retrofit interface and the remote datasource interface with its iTunes implementation live in `:core:network`. Replacing the API means replacing that implementation (and its `Api*` models); feature repositories only map `Api*` to domain. Feature datasources wrap one source (the remote datasource, or a Room DAO) for one domain and return raw `Api*` / `Db*` models in `Result<T>` (`Flow<Result<T>>` for streams). They own `Dispatchers.IO` (`withContext` for suspend, `.flowOn` for Flow).
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
        .flowOn(Dispatchers.IO)
```

  A Flow never throws to its collector: every failure is an emitted `Result.failure`.
- Repositories depend on datasources, never on the API interface or a DAO. They map with `.map { it.toDomain() }` for suspend, and `.map { result -> result.map { it.toDomain() } }` for Flow. They never use `runCatching` or `mapCatching`.
- Mappers are pure extension functions: `fun ApiExercise.toDomain()` / `fun DbExercise.toDomain(): ExerciseDomainModel` (called from repositories) and `fun ExerciseDomainModel.toUiModel(): UiExercise` (called from ViewModels, never from composables). A domain model never carries an `Api*` or `Db*` field.
- Room entities and DAOs live in `:core:database` (`core/database/{entity,dao}`), because a single `AppDatabase` must know every entity. A DAO may return `Flow<List<Db*>>` directly, because only datasources touch it. DAO one-shot reads and writes are `suspend` (Room runs them off the main thread itself), and the local datasource wraps them like an API call:

```kotlin
suspend fun saveExercise(exercise: DbExercise): Result<Unit> =
    withContext(Dispatchers.IO) {
        runCatching { dao.upsert(exercise) }
            .onFailure { if (it is CancellationException) throw it }
    }
```

  Writes return `Result<Unit>`; the repository maps the domain model to `Db*` on the way in (`ExerciseDomainModel.toDb()`) and `Db*` to domain on the way out.
- Failures reach the UI as `Result.failure` and become the `error` field of `UiState`.

### Error handling by layer
DataSources catch (`runCatching` for suspend, `.catch` for Flow). Repositories and use cases propagate `Result<T>` or `Flow<Result<T>>`. ViewModels use `.onSuccess`/`.onFailure`, per emission for a stream. Exceptions are never used for flow control.

---

## 7. Theming

Screens take colours, typography and shapes from `MaterialTheme.colorScheme`, `MaterialTheme.typography` and `MaterialTheme.shapes`. Change the palette in `core/design/theme/Color.kt` and `Theme.kt`, not at call sites. The theme uses dynamic colour on Android 12+ and follows the system dark mode. Never hardcode hex colours, corner radii or font sizes.

---

## 8. Code quality

- Names reveal intent; avoid `Manager`, `Helper`, `Util`, `Handler`. Booleans use `is`/`has`/`can`.
- A class or function does one thing; no boolean flag parameters that change behaviour.
- Guard clauses over nested conditionals. Meaningful literals are named constants.
- Comments explain *why* (hidden constraint, workaround, invariant), never *what*.
- Extract duplicated logic on the third occurrence, not the second.

---

## 9. Testing

- Unit tests: JUnit 4; add MockK and `kotlinx-coroutines-test` when the first ViewModel or use case is written, plus Turbine when the first Flow is tested. ViewModels, use cases and repositories are always tested.
- Tests live in the module that owns the code: network with MockWebServer in `:core:network`, in-memory Room in `:core:database`, use cases, repositories and ViewModels in their feature module.
- An architecture test (Konsist or ArchUnit) fails when presentation imports data, domain imports Android, a feature imports another feature, or `core:*` imports a feature.
- Commands: `./gradlew test`, `./gradlew lint`, `./gradlew connectedAndroidTest`. Gradle wrapper only.
- No CI and no static analysis configured yet.
