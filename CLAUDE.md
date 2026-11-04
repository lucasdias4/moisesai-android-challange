# Moisesai Challange

Android app (Jetpack Compose), package `com.moisesai.challange`, theme `MoisesaiChallangeTheme`.
This is the short list. The full rules are in `ARCHITECTURE.md` (section numbers below); if they disagree, `ARCHITECTURE.md` wins.

## Current state
Every module has code: `:app` (splash, `AppNavHost`), `:core:{common,data-common,design,strings,network,database,player,shared-domain-model}` and `:feature:{songs,songdetails,album}`, each feature with domain, data, ViewModel, navigation and UI. The whole Android page of the Figma design is built (fixed dark scheme, tokens named as in the file; §7): splash, Songs, Player (with its action sheet) and Album. Motion is in: shimmer skeletons for every loading state, a shared cover between lists and the Player, sheets that rise and can be pulled down, a splash with phases, screen transitions (§4 "Animations"). Not done: the Articulat CF font files (commercial, not usable here) and the tablet, watch and Android Auto frames. Local and git-ignored: `PLAN.md`, `misc-notes.md`, `core/network/itunes-api-samples.md`.

## Layering and modules (§2)
`Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API`
Modules by feature (`:feature:songs`, `:feature:songdetails`, `:feature:album`) plus `:core:{design,common,shared-domain-model,network,database,player}`. Layers are packages inside each feature: `feature.<name>.{presentation,domain,data}`.

## Hard rules
- Features never import each other; shared types live in `core:*`; `core:*` never depends on a feature. Presentation never imports data; `domain` has no Android imports and depends only on its feature, `:core:common` and `:core:shared-domain-model` (never `:core:player`); a ViewModel calls use cases, except that it holds `PlayerController` directly. (§2, §4, §5)
- `suspend` by default, `Flow` only for streams. Errors are caught in datasources, travel as `Result<T>`, cancellation is rethrown, and an empty result is a success. (§2, §6)
- `:core:data-common` (`safeCall`, `asSafeResult`, `toAppError`) is for `:core:network` and `:core:database` only; features never depend on it. (§2)
- Domain: a use case holds business rules only and does not re-check what the ViewModel already decides for the UI; values several use cases share live in `domain/model/Constants.kt`. (§5)
- Datasources: concrete (no interface), split by context, return the raw response, no dispatcher switch. The repository maps and owns the injected `DispatcherProvider`. (§6)
- Repositories: chain failing steps with early returns (`getOrElse { return Result.failure(it) }`), no `flatMap` helper; mappers are pure and never switch threads, the repository does (`dispatchers.default` for CPU work); names say where data comes from (`getTrackFromApiAndSaveToCache`) and conditions are named `val`s. (§6)
- DTOs: one file per class, `@Serializable` and `@SerialName`, no default values, nullable only when real data shows it. Fixed query parameters go in `@GET`. (§6)
- Room 3, `Db*`, DAOs and the local datasources live in `:core:database`. Types only a datasource uses are `internal`, and so is its constructor. A multi-table write is one transaction, and rows are saved with `@Upsert`, not a replace. (§6)
- Animations (§4): an `animation` package next to the screen (shared ones in `:core:design/animation`, pace in `animation/spec/ScreenSpec.kt`), one animation per file, a single `Phase` timeline, every constant and animated value commented (meaning and why), animated state read in draw or `graphicsLayer`, never in composition. Each animation file starts with a two-line KDoc (what it does; where it is used and how) and each place that uses one has a one-line comment. Reuse the shared ones before writing a new one. Use the `create-animation` skill.
- Names: track and album; a use case held by a ViewModel (or a test) keeps the `UseCase` postfix on the variable too (`getAlbumWithTracksUseCase`, called as `getAlbumWithTracksUseCase(albumId)`); `id`, `name`, `albumId`, `artUrl`; methods are an action plus what the caller wants (`getTrack`, `searchTracksBySearchText`); parameters named by meaning; models carry their layer (`Ui*`, `*DomainModel`, `Api*`, `Db*`); animation composables and their files end in `Animation` and live in an `animation` package next to their screen; callback holders are `<Feature>Callbacks` in `composable/callback`; components end in `Component` and live in `presentation/composable/component`, each with `@FullPreview` previews in its file. (§3, §4, §8)
- Clean Code applies everywhere (§8): intent-revealing names in plain words (`addAlbumToQueue`, not `widenQueue`), small functions, at most three parameters, no flag arguments, `val` over `var`, no magic values, no dead code, one class per file, `internal` by default, comments explain *why*, an `if`/`else` that runs statements has braces and one line per branch (never `if (a) x() else y()`).
- UI: colours, typography and shapes from `MaterialTheme`; every screen has `@FullPreview` previews wrapped in `MoisesaiChallangeTheme`; text from `R.string.*` in `:core:strings`, XML theme and colours in `:core:design`. (§4, §7)
- Tests: Given / When / Then in both the name (`GIVEN ..., WHEN ..., THEN ...`, keywords uppercase) and the body, one action each; fixtures from the real API; none that pass with the code broken. (§9, `write-unit-tests`, `write-ui-tests`). The pyramid targets are 100% unit, 50% functional, 30% end to end (README "Testing strategy"): write the test in the same commit as the code.

## Working agreements
- A `Stop` hook (`.claude/settings.json`, script in `.claude/hooks/ktlint-check.sh`) runs `./gradlew ktlintCheck` when you finish and a Kotlin file changed. If it reports violations, run `./gradlew ktlintFormat`, fix what is left, then stop.
- Code style is ktlint (`.editorconfig`, `android_studio` style). Run `./gradlew ktlintFormat` before `ktlintCheck`; the rule exceptions (underscore test names, `view_model` package) live in `.editorconfig`.
- Rules are defined in `ARCHITECTURE.md`. To change one: edit it there first, then its short version here and the examples in the skills, then search for the old wording.
- Small steps, one commit each, green on `./gradlew clean assembleDebug testDebugUnitTest lintDebug ktlintCheck connectedDebugAndroidTest`. Suggest a short commit message; do not commit unless asked.
- Temporary code lives in one `Temporary*.kt` file marked `TEMPORARY` (§11).
- Room tests are instrumented tests; their names use underscores (`GIVEN_a_..._WHEN_..._THEN_...`) because DEX before version 40 forbids spaces (§9).
- Question a request that conflicts with a rule or a requirement, say why, then follow the user's decision.

## Skills (`.claude/skills/`)
`create-feature-module`, `create-compose-screen`, `create-ui-component`, `create-animation`, `add-data-endpoint`, `write-unit-tests`, `write-ui-tests`, `clean-code-review`.
