# Moisesai Challange — Architecture Reference

## 1. Current state

- One module: `:app`. Package `com.moisesai.challange`.
- Present: `MainActivity`, theme (`app/src/main/java/com/moisesai/challange/ui/theme/{Color,Theme,Type}.kt`), `@FullPreview` (`ui/preview/FullPreview.kt`), Compose, Material 3.
- Not present yet: dependency injection, networking, database (Room), ViewModels, navigation, domain/data code.
- Versions and SDKs: `gradle/libs.versions.toml` and `app/build.gradle.kts`. JDK per `gradle/gradle-daemon-jvm.properties`.

Everything below the next heading is the convention for code **as it is added**. When a section needs a dependency that is missing, add it to `gradle/libs.versions.toml` first.

---

## 2. Layers

```text
Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API
```

- **Presentation -> Domain:** ViewModels never call the API or DataSources. They go through use cases.
- **Domain -> Data:** domain knows only repository interfaces; implementations live in `data`.
- **Packages first:** while there is one module, layers are packages under `com.moisesai.challange`:

```text
com.moisesai.challange
├── ui/theme/                    # exists
├── ui/preview/                  # exists: @FullPreview
├── ui/components/               # shared composables (see create-ui-component)
├── <feature>/                   # presentation, one package per feature
│   ├── composable/  model/  view_model/  navigation/  mapper/
├── domain/                      # model/, repository/, usecase/  (pure Kotlin, no Android imports)
└── data/                        # datasource/, remote/{api,dto}, local/{dao,entity}, mapper/, repository/
```

Features do not import each other's packages. Shared state goes through a `domain` repository or use case.

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

Package names are singular, lowercase, no underscores for the app-level layers. Feature subpackages use the names above (`view_model` with underscore is the one exception).

---

## 4. Presentation

- `@HiltViewModel` once Hilt is added; until then a plain `ViewModel` created with `viewModel { ... }`.
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
- Repository interfaces live in `domain/repository`.

## 6. Data

- DataSources wrap one source (the API, or a Room DAO) for one domain and return raw `Api*` / `Db*` models in `Result<T>` (`Flow<Result<T>>` for streams). They own `Dispatchers.IO` (`withContext` for suspend, `.flowOn` for Flow).
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
- Room entities and DAOs live in `data/local/{entity,dao}`. A DAO may return `Flow<List<Db*>>` directly, because only datasources touch it. DAO one-shot reads and writes are `suspend` (Room runs them off the main thread itself), and the local datasource wraps them like an API call:

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

Screens take colours, typography and shapes from `MaterialTheme.colorScheme`, `MaterialTheme.typography` and `MaterialTheme.shapes`. Change the palette in `ui/theme/Color.kt` and `Theme.kt`, not at call sites. The theme uses dynamic colour on Android 12+ and follows the system dark mode. Never hardcode hex colours, corner radii or font sizes.

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
- Commands: `./gradlew test`, `./gradlew lint`, `./gradlew connectedAndroidTest`. Gradle wrapper only.
- No CI and no static analysis configured yet.
