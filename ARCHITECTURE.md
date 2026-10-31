# Moisesai Challange — Architecture Reference

## 1. Current state

- One module: `:app`. Package `com.moisesai.challange`.
- Present: `MainActivity`, theme (`app/src/main/java/com/moisesai/challange/ui/theme/{Color,Theme,Type}.kt`), `@FullPreview` (`ui/preview/FullPreview.kt`), Compose, Material 3.
- Not present yet: dependency injection, networking, ViewModels, navigation, domain/data code.
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
└── data/                        # datasource/, remote/{api,dto}, mapper/, repository/
```

Features do not import each other's packages. Shared state goes through a `domain` repository or use case.

---

## 3. Naming by layer

| Layer | Convention | Example |
|---|---|---|
| Presentation | prefix `Ui` | `UiExercise` |
| Domain | suffix `DomainModel` | `ExerciseDomainModel` |
| Data / API | prefix `Api` | `ApiExercise` |

Package names are singular, lowercase, no underscores for the app-level layers. Feature subpackages use the names above (`view_model` with underscore is the one exception).

---

## 4. Presentation

- `@HiltViewModel` once Hilt is added; until then a plain `ViewModel` created with `viewModel { ... }`.
- State: `StateFlow<UiState>` (`_uiState` private, `uiState` exposed), consumed with `collectAsStateWithLifecycle()` in the navigation graph, not in the screen composable.
- `data class UiState` when states overlap; a `sealed class` when they are mutually exclusive. `isLoading` and `error` exist on every `UiState`.
- ViewModels hold UI logic only (loading/error state, domain -> UI mapping). Business rules belong in use cases.
- Navigation is driven by lambdas passed in from the root `NavHost`; ViewModels and feature composables never own the `NavController`.
- Screen composables are stateless: `uiState` plus callbacks. `modifier: Modifier = Modifier` is the first optional parameter.
- Top-level screen composables have `@FullPreview` previews wrapped in `MoisesaiChallangeTheme { }`.

## 5. Domain

- Pure Kotlin, no Android imports. Models end in `DomainModel`.
- One public method per use case; verb names (`GetExercisesUseCase`); `Get` never persists, use `Save`/`Submit`.
- `operator fun invoke(...): Flow<T>` for streams, `suspend operator fun invoke(...): T` for one-shot calls.
- Repository interfaces live in `domain/repository`.

## 6. Data

- DataSources wrap the API for one domain and return raw `Api*` models in `Result<T>`. They own `Dispatchers.IO` (`withContext` for suspend, `.flowOn` for Flow).
- `runCatching` lives in datasources only, and rethrows `CancellationException`:

```kotlin
runCatching { api.getExercises(lessonId) }
    .onFailure { if (it is CancellationException) throw it }
```

- Repositories depend on datasources, never on the API interface. They map with `.map { it.toDomain() }` and never use `runCatching` or `mapCatching`.
- Mappers are pure extension functions: `fun ApiExercise.toDomain(): ExerciseDomainModel` (called from repositories) and `fun ExerciseDomainModel.toUiModel(): UiExercise` (called from ViewModels, never from composables).
- Failures reach the UI as `Result.failure` and become the `error` field of `UiState`.

### Error handling by layer
DataSources catch. Repositories and use cases propagate `Result<T>`. ViewModels use `.onSuccess`/`.onFailure`. Exceptions are never used for flow control.

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

- Unit tests: JUnit 4; add MockK and `kotlinx-coroutines-test` when the first ViewModel or use case is written. ViewModels, use cases and repositories are always tested.
- Commands: `./gradlew test`, `./gradlew lint`, `./gradlew connectedAndroidTest`. Gradle wrapper only.
- No CI and no static analysis configured yet.
