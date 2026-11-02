# Moisesai Challange — Architecture Reference

**This file is the single source of the project's rules.** `CLAUDE.md` holds one-line versions that point here, and the skills hold procedures and examples. To change a rule, edit it here first, then update its one-line version in `CLAUDE.md` and any example in a skill, and search the repository for the old wording.

## 1. Current state

- **Modules:** `:app`, `:core:common`, `:core:data-common`, `:core:design`, `:core:strings`, `:core:network`, `:core:database` and `:core:shared-domain-model` (JVM-only) have code. Empty so far: `:core:player` and the three `:feature:*`. Package `com.moisesai.challange`.
- **Done:** `build-logic` convention plugins, Hilt in `:app`, test tooling, the Aurora Neon theme and `@FullPreview` in `:core:design`, the animated splash screen in `:app` (`splash` package), `AppError` and `DispatcherProvider` in `:core:common` (the provider is for repositories and has no user yet), the datasource error helpers in `:core:data-common`, the remote layer in `:core:network` and the local layer in `:core:database` (section 6), the shared `TrackDomainModel` in `:core:shared-domain-model` and `AlbumDomainModel` in `:feature:album` (`domain/model`, because only the album feature uses it); their fields match `ApiTrack`/`DbTrack` and `ApiAlbum`, so the mappers are one-to-one.
- **Not present yet:** repositories, use cases, ViewModels, navigation and the real screens. `MainActivity` shows a `TemporaryDataCheck` (section 11).
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
:core:design               theme (Compose and the XML window theme), colours, fonts, @FullPreview, shared components
:core:strings              every user-facing string (res/values only, no code)
:core:common               AppError, DispatcherProvider: what every layer may use
:core:data-common          safeCall, asSafeResult, toAppError: error handling for datasources only
:core:shared-domain-model  pure Kotlin shared domain models only (TrackDomainModel): no logic, no Android
:core:network              OkHttp/Retrofit setup, ItunesApi, Api* DTOs, remote datasources (track, album)
:core:database             Room 3 AppDatabase, Db* entities, DAOs, local datasources (track, search, recent track)
:core:player               playback engine: PlayerController interface + Media3 ExoPlayer implementation (knows no feature or iTunes code)
```

### Feature package layout

```text
com.moisesai.challange.feature.<name>
├── presentation/   composable/  model/  view_model/  navigation/  mapper/
├── domain/         usecase/  repository/ (interfaces)   # pure Kotlin, no Android imports
└── data/           datasource/  repository/  mapper/
```

Core packages: `com.moisesai.challange.core.{design,common,datacommon,shareddomainmodel,network,database}`.

### Dependency rules

- `feature:*` depends on `core:*`. Features never depend on or import each other. `:app` connects them with type-safe routes and lambdas (ids only: Player receives a track id, Album an album id).
- `:core:data-common` is only for `:core:network` and `:core:database`. Features never depend on it, so a repository or a use case cannot call `safeCall` (Gradle enforces this, unlike the layer rules). `:core:common` holds what every layer may see (`AppError`, `DispatcherProvider`).
- `:core:strings` holds all user-facing text and depends on nothing. `:app` and the features depend on it; `:core:design` does not.
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

**Animations** end in `Animation` (the composable and its file, for example `EqualizerBarsAnimation.kt`). Where they live and how they are written is in section 4, "Animations".

**Components** (a reusable piece of a screen: a field, a card, a row, a chip, a state) end in `Component`, the composable and its file (`SearchFieldComponent.kt`), and live in a `component` package inside `composable`. The package layout of `presentation/composable` is in section 4.

Package names are singular, lowercase, no underscores for modules and layers (`feature.songs.presentation`). Presentation subpackages use the names in section 2 (`view_model` with underscore is the one exception).

### Vocabulary

One word for one thing, in every layer:

- **`track` and `album`**, never `song` or `collection` (iTunes calls an album a "collection" and a track a "song" in places; our code does not).
- **`id` and `name`** are the model's own identity. A reference to another model is prefixed: `albumId`, `albumName`.
- **Short, plain property names:** `artUrl`, not `artworkUrl100`; `artistName`, not `artistNameText`.
- **Names are ours, wire names stay on the wire.** A DTO property uses the app's word and keeps the JSON key with `@SerialName("collectionId") val id: Long`. A Retrofit parameter is named by meaning (`searchText`) and keeps the wire key in `@Query("term")`.
- **Methods are an action plus what the caller wants** (section 8): `searchTracksBySearchText`, `getTrack`, `getAlbumWithTracks`.
- **Storage follows the API's names.** An entity has the same property names as its DTO (`DbTrack` and `ApiTrack` both have `id`, `name`, `albumId`, `artUrl`), table names are singular snake_case (`track`, `saved_search_track`), and a time column ends in `Millis` (`fetchedAtMillis`, like `trackTimeMillis`). Local datasources use `get` (read), `save` (write) and `observe` (stream); the name says what is read or written (`getSavedSearch`, `saveSearch`).

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
- `presentation/composable` is split by role: `screen` (the parent screen with its previews, and one package per section of it: `songs_list_section` with a `variant` package, one `SongsListFor<State>Variant` per state the list can be in, and an `extension` package for what the list needs; `big_header_section`, `compact_header_section` and `search_field_section`; and `helper` for behaviour the screen needs but does not draw, such as the ambient tint and the load-more effect), `component` (every `*Component`, one per file, plus the small types only they use, with `layout` for the measurements, `modifier` for custom modifiers, `icon` for vector graphics and `preview_helper` for sample data and the surface previews draw on), `animation` (one `*Animation` per file; an animation that needs more than one file gets its own package, such as `reveal`, `content_swap` and `header_collapse`) and `model` (what composables take or share: the action holder `SongsActions`, contexts such as `ListContext` and `ScrollContext`, and the small types only composables use).
- Every component has `@FullPreview` previews at the bottom of its own file (private parts such as a badge or a button have their own too), wrapped in the `PreviewSurface` of the `preview_helper` package, with the sample data from there. A preview shows a component in its resting look: arrival animations start already finished.

### Animations

The reference implementation is the splash in `:app` (`splash/animation`). Follow the `create-animation` skill.

- **Package and names.** Animations live in an `animation` package next to the screen that uses them (`splash/animation`). One animation per file; the composable and its file end in `Animation` (`EqualizerBarsAnimation`). They are `internal`, and the screen package depends on `animation`, never the other way round.
- **One timeline.** An animation with steps has one `<Screen>Phase` enum (`SplashPhase`) in the `animation` package, each phase with its `durationMillis`. The screen walks the phases in order with a single `LaunchedEffect`; every animation is stateless and only reacts to the current phase (`phase >= Collapsing`). No animation keeps its own timer for the whole sequence.
- **Comments.** Animation numbers do not explain themselves, so every constant, every animated state and every non-obvious call has a short, direct comment saying what the value does in the picture (its range, `0 = hidden, 1 = shown`) and why it is that way. This is the *why* rule of section 8 applied to motion: the comment is never a restatement of the code.
- **Performance.** Read animated values in `graphicsLayer { }` or in a draw block (`Canvas`, `drawBehind`), not in composition, so a frame redraws and never recomposes. Draw many identical shapes on one `Canvas`. Animate `alpha`, scale and translation, not layout.
- **Theme and text.** Colours from `MaterialTheme`, durations, sizes and ratios as named constants, text from `:core:strings`. Never a hex colour or a magic number.
- **Accessibility.** An animation that is decoration adds no semantics. Text that is animated piece by piece (letter by letter) is read once, with a `contentDescription` on the container.
- **Tests.** An instrumented test sets `mainClock.autoAdvance = false` when an infinite animation exists, and advances the clock by the sum of the phase durations: it must fail if the sequence never finishes or finishes too early. Test the visible text and the finish callback, not the pixels.

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
├── extension/   extension functions: ApiResponseExtensions, SafeRemoteCallExtension, RemoteErrorExtension
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
- The shared error handling is one helper, `safeRemoteCall { ... }`, not repeated in each method. It is `safeCall` from `:core:data-common` plus the HTTP rule (`toRemoteError`):

```kotlin
suspend fun getTrack(trackId: Long): Result<ApiResponse> =
    safeRemoteCall { api.lookupTrack(trackId) }
```

**Errors**
- `AppError` has two cases: `Network` (no connection, a dropped connection, or any non-2xx HTTP status) and `Unknown` (a body that does not parse, or a required field that is missing). Cancellation is rethrown, never turned into a failure.
- **An empty result is a success with an empty list**, not an error. iTunes answers an empty search and an unknown id with HTTP 200 and no results, so there is no "empty" error.

**Extensions (`extension/`)**
- Files are named `<Subject>Extensions.kt` when they hold several functions (`ApiResponseExtensions.kt`) and `<Subject>Extension.kt` when they hold one (`SafeRemoteCallExtension.kt`, `RemoteErrorExtension.kt`).
- `internal` unless another module needs it. `tracks()` and `album()` are public because every feature's repository uses them; `safeRemoteCall` and `toRemoteError` are internal.
- The mapping `Throwable -> AppError` (`toAppError`) and the cancellation rule live once, in `:core:data-common`.

**Visibility.** The datasource classes are public and their constructors are `internal`: `ItunesApi` and the other types only the datasources use never leave the module.

### Local layer (`:core:database`)

```text
core/database
├── AppDatabase.kt   the single Room database (internal)
├── entity/          Db* entities
├── dao/             DAOs (internal)
├── datasource/      concrete local datasources, one per context (TrackLocalDataSource, SavedSearchLocalDataSource, RecentlyPlayedTrackLocalDataSource)
└── di/              Hilt module
```

**Room 3** (`androidx.room3`): coroutine-first, driver-based (`AndroidSQLiteDriver`) and KSP-only. DAO functions are `suspend` or return a `Flow`. `schemaDirectory` exports the schema to `core/database/schemas/` on every build; that folder is git-ignored. When the database gets a released version, keep that version's schema file (un-ignore it), because a migration is validated against it. There is no destructive-migration fallback; a schema change needs a version bump and a migration.

**Tables.** `track` (every track stored locally, one copy each), `saved_search` (one row per saved search: its text and `fetchedAtMillis`, so the repository can judge freshness), `saved_search_track` (one track of a saved search, at a `position`) and `recently_played_track` (one row per played track, `playedAtMillis`). Foreign keys cascade on delete.

**Entities.** `DbTrack` and `DbSavedSearch` are public because the datasources return and accept them; `DbSavedSearchTrack` and `DbRecentlyPlayedTrack` are storage details and stay `internal`. The saved search's text is its key exactly as typed; normalising it is the repository's job.

**DAOs** are `internal`, one per concept (`TrackDao`, `SavedSearchDao`, `RecentlyPlayedTrackDao`). Their verbs say what the write does, because the kinds behave differently: `get...`, `upsert...` (insert or update in place), `insert...` (a plain insert, an error if the row exists), `delete...` and `observe...`. A name says what a method really does: `deleteSavedSearch` deletes the saved search and, through the foreign key, all its saved tracks. Paging is a SQL `LIMIT` and `OFFSET` read, so a page is one query. Writes that must not replace a row use `@Upsert`: a replace deletes the row first and the cascade would delete the rows that point to it. The datasources keep `save...` (a datasource call is one logical save, which may be several writes).

**Datasources** follow the rules in the remote layer (concrete, no interface, no dispatcher switch) and:
- Methods: `getTrack`, `saveTrack` | `getSavedSearch`, `getSavedSearchTracksPage(searchText, limit, offset)`, `saveSearch(savedSearch, tracks)` | `observeRecentlyPlayedTracks(limit)`, `saveRecentlyPlayedTrack(track, playedAtMillis)`.
- The class is public and its constructor is `internal`, so Room types (`AppDatabase`, the DAOs) never leave the module.
- A write that touches several tables runs in one `database.withWriteTransaction { ... }`, so it is all or nothing.
- Timestamps are passed in by the caller (the repository owns the clock), so a datasource is deterministic.
- A Flow is built lazily, inside `flow { emitAll(dao.observe...) }`, because Room checks that the database is open when the DAO builds its Flow; built eagerly, that failure would be thrown to the caller instead of emitted.
- `safeCall { ... }` (from `:core:data-common`) wraps a suspend call and `asSafeResult()` wraps a Flow, so every failure is a `Result.failure` (and cancellation is rethrown).

### Streams, repositories and mappers

- `runCatching` lives in suspend datasources only (inside `safeCall`), and rethrows `CancellationException`.
- A stream datasource cannot use `runCatching`, because it does not see errors thrown during later emissions. It uses `.catch`, wrapped once in `asSafeResult()`: a Flow never throws to its collector, every failure is an emitted `Result.failure`.
- Repositories depend on datasources, never on the API interface or a DAO. They map with `.map { it.toDomain() }` for suspend, and `.map { result -> result.map { it.toDomain() } }` for Flow. They never use `runCatching` or `mapCatching`.
- Mappers are pure extension functions: `fun ApiTrack.toDb()` / `fun DbTrack.toDomain(): TrackDomainModel` (called from repositories) and `fun TrackDomainModel.toUiModel(): UiTrack` (called from ViewModels, never from composables). A domain model never carries an `Api*` or `Db*` field.
- Failures reach the UI as `Result.failure` and become the `error` field of `UiState`.

### Error handling by layer
DataSources catch (`runCatching` for suspend, `.catch` for Flow). Repositories and use cases propagate `Result<T>` or `Flow<Result<T>>`. ViewModels use `.onSuccess`/`.onFailure`, per emission for a stream. Exceptions are never used for flow control.

---

## 7. Theming

Screens take colours, typography and shapes from `MaterialTheme.colorScheme`, `MaterialTheme.typography` and `MaterialTheme.shapes`. Change the palette in `core/design/theme/Color.kt` and `Theme.kt`, not at call sites. The visual identity is **Aurora Neon**: glass surfaces and soft glows with one fixed dark colour scheme (lime and magenta on near-black) and the Space Grotesk typeface, with Inter Black for the display styles (Space Grotesk has no heavier weight than Bold); both are bundled in `:core:design` (`res/font`). There is no dynamic colour and no light scheme, so the system bars are drawn for dark content. The XML half of the theme (`themes.xml` and the one colour it needs, `colors.xml`) lives next to `Color.kt` in `:core:design`. All user-facing text lives in `:core:strings`, one `strings_<screen>.xml` file per screen with a `<screen>_` prefix on every name, and is read with `stringResource(R.string.<name>)` using that module's `R`. The agreed visual concept (screens, states, tokens, motion and the order to build it) is in `app/visual-concept/`; it is a reference and is not compiled. Never hardcode hex colours, corner radii, font sizes or user-facing text.

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

- **Comments explain why** (hidden constraint, workaround, invariant), never what. Good code does not need a *what* comment. Animation code is covered by the rule in section 4: it comments the meaning and the reason of every value, because motion numbers cannot be read from the code alone.
- **No commented-out code, no journal or banner comments, no noise** (`// constructor`). Version control keeps history.
- **KDoc only on public API whose contract is not obvious.**
- **Formatting is the Kotlin official style.** Related code stays close together; blank lines separate concepts, not lines of one idea. No unused imports, parameters or code (delete it; YAGNI).

### Error handling

- Errors are values at layer boundaries (`Result<T>`, `AppError`); coroutine cancellation is never swallowed. Do not catch an exception you cannot handle, and never leave a `catch` block empty.

### Tests (F.I.R.S.T.)

- **Fast, Independent, Repeatable, Self-validating, Timely.** No test depends on another or on the network, the clock or the order of execution.
- **One concept per test**, with the name and the body both in **Given / When / Then** form (section 9).
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

**Where tests live.** In the module that owns the code: remote datasources with MockWebServer in `:core:network`, local datasources on an in-memory Room database in `:core:database`, use cases, repositories and ViewModels in their feature module.

**Room tests are instrumented tests** (`src/androidTest`), because Room runs on the device's real SQLite; they use an in-memory database, so nothing is written to disk and each test starts empty. A multi-write method also gets a test where the second write fails and the first must be rolled back.

**Instrumented test names use underscores.** DEX before version 40 forbids spaces in method names and `minSdk` is 24, so an instrumented test keeps the uppercase keywords and joins the words with underscores: `GIVEN_a_saved_track_WHEN_getting_it_THEN_every_field_is_returned`. JVM unit tests keep the sentence form. ViewModels, use cases and repositories are always tested.

**Fixtures come from the real API.** Capture them with read-only calls, store them in `src/test/resources/fixtures/`, and trim them only by removing items from a list. Never invent a response that the API could send. Synthetic JSON is for cases the API will not produce (a malformed body, a missing required field), written inline in the test.

**What a remote datasource test covers:** the request that is sent (path and query), the mapping of a real response, an empty result, and each error mapping (HTTP status, dropped connection, malformed body, missing required field). Pure helpers get their own tests with no network: `safeRemoteCall` in `:core:network`, and `safeCall`, `asSafeResult` and `toAppError` in `:core:data-common`.

**How tests are written: Given / When / Then.** Every unit test body has three parts, each marked with a comment: `// Given` (the setup: inputs, fakes, the response the server will give), `// When` (the one action under test, a single call) and `// Then` (the assertions). One `When` per test is what makes it one concept. `Given` may use the class's other methods to reach the state; only the `When` action is under test. The name states the same three parts as one sentence in backticks: `` `GIVEN <state>, WHEN <action>, THEN <outcome>` ``, with the three keywords in uppercase and the rest in lowercase, for example `` `GIVEN an unknown track id, WHEN getting the track, THEN the response is successful and has no results` ``. No logic in tests: no loops or conditionals around assertions.

**What to test, and where.** Test through the public entry points. A class with its own logic gets a test class in a package that mirrors the production package (`datasource`, `extension`); a class that is only configuration or a data holder (an annotated service interface, a DTO, the `Json` setup, Hilt wiring) has no test class of its own and is covered through the entry points that use it, with real fixtures. Test helpers live in a `support` package and exist only when two or more test classes use them; a helper with one user is private in that class.

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
- It is listed when you hand work over, and it is removed in the step that makes it unnecessary. Today: `TemporaryDataCheck.kt` in `:app` (with the `:core:network` and `:core:database` dependencies), removed with the first real screen.
- It is exempt from the `R.string.*` and `@FullPreview` rules while it exists, and from nothing else.
