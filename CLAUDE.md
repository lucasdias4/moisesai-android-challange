# Moisesai Challange

Android app, Jetpack Compose, package `com.moisesai.challange`, theme `MoisesaiChallangeTheme`.
Full reference: `ARCHITECTURE.md`.

## Current state
One Gradle module, `:app`, today. No DI, no network, no ViewModel and no navigation yet. The theme and `@FullPreview` (`core/design/.../preview/FullPreview.kt`, phone, dark, small and tablet previews) live in `:core:design`. The target is the feature-based module layout below, built step by step; new code follows it. The iTunes challenge plan is in `PLAN.md`.

## Modules (by feature, not by layer)
`:app` (Application, NavHost, DI root, splash), `:feature:<name>` (songs, songdetails, album), `:core:design`, `:core:common`, `:core:shared-model` (shared pure-Kotlin models), `:core:network`, `:core:database`, `:library:player` (reusable Media3 playback engine behind a `PlayerController`).
Each feature module holds its own layers as **packages**: `com.moisesai.challange.feature.<name>.{presentation,domain,data}`. There is no global `:domain` or `:data` module.

## Layering (the convention new code follows)
`Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API`

## Hard rules
- Features never import each other: `:app` connects them with routes and lambdas. Shared types live in `core:*` only. `core:*` and `library:*` never depend on a feature.
- Inside a feature, `presentation -> domain <- data`; presentation never imports data. Repository interfaces and use cases live in the feature's `domain`; implementations in its `data`.
- `Api*` DTOs and the iTunes client live in `:core:network`; `Db*` entities and DAOs live in `:core:database` (one Room `AppDatabase`); features own their datasources and repositories over them.
- A ViewModel calls use cases, never repositories, datasources or the API. `domain` has no Android imports.
- `suspend` by default (API calls, DB one-shot reads and all DB writes); `Flow` only for a stream (observed DB queries, values that change over time). Never wrap a single value in a `Flow`.
- Failures are caught in datasources (`runCatching` for suspend, `.catch` for Flow), always rethrowing `CancellationException`; everything above propagates `Result<T>` (`Flow<Result<T>>` for streams).
- Mapping: data -> domain in the repository, domain -> `Ui*` in the ViewModel. Composables never map and never collect a domain Flow.
- Models carry their layer in the name: `Ui*`, `*DomainModel`, `Api*` (network), `Db*` (Room).
- Colours, typography and shapes come from `MaterialTheme` (`core/design/theme/`), never hex literals or inline `TextStyle`.
- Every screen composable has `@FullPreview` previews wrapped in `MoisesaiChallangeTheme { }`.
- User-facing text comes from `R.string.*`.
- Comments explain *why*, never *what*.

## Skills (procedures, in `.claude/skills/`)
`create-feature-module`, `create-compose-screen`, `create-ui-component`, `add-data-endpoint`.
