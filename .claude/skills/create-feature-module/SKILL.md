---
name: create-feature-module
description: Scaffold a new `:feature:<name>` Gradle module with its presentation, domain and data packages, build file, DI module and settings include. Use when adding a feature (songs, songdetails, album, ...) before filling its screen or data.
---

Create the module that owns one feature end to end, following `ARCHITECTURE.md` §2. Layers are packages inside the module; there is no global `:domain` or `:data` module.

## When to use

- A new feature that needs its own screen, use cases or data
- The feature module does not exist yet

## When NOT to use

- A screen inside an existing feature: use `create-compose-screen`
- A shared component: use `create-ui-component`
- A new endpoint for an existing feature: use `add-data-endpoint`

## Input

Feature name: lower case, no underscores (e.g. `songs`). It becomes the module `:feature:<name>` and the package `com.moisesai.challange.feature.<name>`.

## Steps

1. **Check for conflicts.** Confirm `feature/<name>` and the include do not already exist.
2. **Register the module** in `settings.gradle.kts`: `include(":feature:<name>")`.
3. **Build file** `feature/<name>/build.gradle.kts`: apply `id("moisesai.android.feature")` and set the `namespace`. The plugin already provides Android library, Compose, Hilt/KSP and the `:core:design`, `:core:common` and `:core:shared-domain-model` dependencies. Add `:core:network`, `:core:database` or `:core:player` only when the feature uses them, in the feature's own build file. Never depend on another `:feature:*` module.
4. **Package skeleton** under `feature/<name>/src/main/java/com/moisesai/challange/feature/<name>/`:

   ```text
   presentation/   composable/  model/  view_model/  navigation/  mapper/
   domain/         usecase/  repository/
   data/           datasource/  repository/  mapper/
   ```

   Create only the folders the feature uses now; add the rest when a file needs them. Add test source sets for `domain` and `data` logic and ViewModels.
5. **DI module** `feature/<name>/.../di/<Name>Module.kt` binding repository interfaces to their implementations, once Hilt is configured.
6. **Wire into `:app`**: add `implementation(project(":feature:<name>"))` and register the feature's navigation graph in the root `NavHost`. This is the only place features meet; routes carry ids only.
7. **Fill it**: screens with `create-compose-screen`, data with `add-data-endpoint`.
8. **Clean code check** (`ARCHITECTURE.md` §8): the module contains only what is used now: no placeholder classes, no empty packages, no unused dependencies.

## Report

Files created, the module's dependencies, and what is left (graph registration, DI binding, tests).

## Hard constraints

- A feature module never depends on another feature module
- `presentation` never imports `data`; `domain` has no Android imports
- Shared models go to `:core:shared-domain-model`, shared UI to `:core:design`, entities to `:core:database`, DTOs to `:core:network`
