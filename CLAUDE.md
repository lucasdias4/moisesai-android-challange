# Moisesai Challange

Android app, Jetpack Compose, package `com.moisesai.challange`, theme `MoisesaiChallangeTheme`.
Full reference: `ARCHITECTURE.md`.

## Current state
One Gradle module, `:app`. No DI, no network, no ViewModel and no navigation yet. Layers are **packages inside `:app`** until a split is worth it. `@FullPreview` (`ui/preview/FullPreview.kt`) covers phone, dark, small and tablet previews.

## Layering (the convention new code follows)
`Compose UI -> ViewModel -> UseCase -> Repository -> DataSource -> API`

## Hard rules
- A ViewModel calls use cases, never repositories, datasources or the API. `domain` has no Android imports.
- Failures are caught in datasources with `runCatching`, which rethrows `CancellationException`; everything above propagates `Result<T>`.
- Models carry their layer in the name: `Ui*`, `*DomainModel`, `Api*`.
- Colours, typography and shapes come from `MaterialTheme` (`ui/theme/`), never hex literals or inline `TextStyle`.
- Every screen composable has `@FullPreview` previews wrapped in `MoisesaiChallangeTheme { }`.
- User-facing text comes from `R.string.*`.
- Comments explain *why*, never *what*.

## Skills (procedures, in `.claude/skills/`)
`create-compose-screen`, `create-ui-component`, `add-data-endpoint`.
