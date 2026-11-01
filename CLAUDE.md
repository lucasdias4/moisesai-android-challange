# Moisesai Challange

Android app (Jetpack Compose), package `com.moisesai.challange`, theme `MoisesaiChallangeTheme`.
This is the short list. The full rules are in `ARCHITECTURE.md` (section numbers below); if they disagree, `ARCHITECTURE.md` wins.

## Current state
Code in `:app`, `:core:common`, `:core:data-common`, `:core:design`, `:core:strings`, `:core:network`, `:core:database`, `:core:shared-domain-model` (`TrackDomainModel`), and `AlbumDomainModel` in `:feature:album`. Empty so far: `:core:player`, `:feature:songs` and `:feature:songdetails`. The visual identity is Aurora Neon (fixed dark scheme, Space Grotesk and Inter Black; §7) and `:app` has the animated splash (`splash` package). `MainActivity` shows the splash and then a `TemporaryDataCheck` (delete it with the first real screen). Local and git-ignored: `PLAN.md`, `misc-notes.md`, `core/network/itunes-api-samples.md`.

## Layering and modules (§2)
`Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API`
Modules by feature (`:feature:songs`, `:feature:songdetails`, `:feature:album`) plus `:core:{design,common,shared-domain-model,network,database,player}`. Layers are packages inside each feature: `feature.<name>.{presentation,domain,data}`.

## Hard rules
- Features never import each other; shared types live in `core:*`; `core:*` never depends on a feature. Presentation never imports data; `domain` has no Android imports; a ViewModel calls use cases only. (§2)
- `suspend` by default, `Flow` only for streams. Errors are caught in datasources, travel as `Result<T>`, cancellation is rethrown, and an empty result is a success. (§2, §6)
- `:core:data-common` (`safeCall`, `asSafeResult`, `toAppError`) is for `:core:network` and `:core:database` only; features never depend on it. (§2)
- Datasources: concrete (no interface), split by context, return the raw response, no dispatcher switch. The repository maps and owns the injected `DispatcherProvider`. (§6)
- DTOs: one file per class, `@Serializable` and `@SerialName`, no default values, nullable only when real data shows it. Fixed query parameters go in `@GET`. (§6)
- Room 3, `Db*`, DAOs and the local datasources live in `:core:database`. Types only a datasource uses are `internal`, and so is its constructor. A multi-table write is one transaction, and rows are saved with `@Upsert`, not a replace. (§6)
- Animations (§4): an `animation` package next to the screen, one animation per file, a single `Phase` timeline, every constant and animated value commented (meaning and why), animated state read in draw or `graphicsLayer`, never in composition. Use the `create-animation` skill.
- Names: track and album; `id`, `name`, `albumId`, `artUrl`; methods are an action plus what the caller wants (`getTrack`, `searchTracksBySearchText`); parameters named by meaning; models carry their layer (`Ui*`, `*DomainModel`, `Api*`, `Db*`); animation composables and their files end in `Animation` and live in an `animation` package next to their screen. (§3, §8)
- Clean Code applies everywhere (§8): intent-revealing names, small functions, at most three parameters, no flag arguments, `val` over `var`, no magic values, no dead code, one class per file, `internal` by default, comments explain *why*.
- UI: colours, typography and shapes from `MaterialTheme`; every screen has `@FullPreview` previews wrapped in `MoisesaiChallangeTheme`; text from `R.string.*` in `:core:strings`, XML theme and colours in `:core:design`. (§4, §7)
- Tests: Given / When / Then in both the name (`GIVEN ..., WHEN ..., THEN ...`, keywords uppercase) and the body, one action each; fixtures from the real API; none that pass with the code broken. (§9, `write-unit-tests`)

## Working agreements
- Rules are defined in `ARCHITECTURE.md`. To change one: edit it there first, then its short version here and the examples in the skills, then search for the old wording.
- Small steps, one commit each, green on `./gradlew clean assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest`. Suggest a short commit message; do not commit unless asked.
- Temporary code lives in one `Temporary*.kt` file marked `TEMPORARY` (§11).
- Room tests are instrumented tests; their names use underscores (`GIVEN_a_..._WHEN_..._THEN_...`) because DEX before version 40 forbids spaces (§9).
- Question a request that conflicts with a rule or a requirement, say why, then follow the user's decision.

## Skills (`.claude/skills/`)
`create-feature-module`, `create-compose-screen`, `create-ui-component`, `create-animation`, `add-data-endpoint`, `write-unit-tests`, `clean-code-review`.
