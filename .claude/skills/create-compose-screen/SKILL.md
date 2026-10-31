---
name: create-compose-screen
description: Scaffold a Moisesai Challange Compose screen - UiState, Ui model (prefix `Ui`), optional mapper, ViewModel, stateless screen composable, navigation graph and previews. Use when creating a new screen or feature.
---

Scaffold a new screen following `ARCHITECTURE.md` §2 and §4. A feature is a Gradle module (`:feature:<name>`) whose presentation, domain and data are packages. If the module does not exist yet, create it first with `create-feature-module`; this skill fills the `presentation` package.

---

## When to Use

- Creating a new screen or feature
- A screen that needs a ViewModel, state and a Compose screen together

## When NOT to Use

- A reusable UI component: use `create-ui-component`
- A small composable added to an existing screen: edit the file directly

---

## Input

- Feature name (lower case, no underscores, e.g. `songs`; it is the module and package name)
- A short description of what the screen displays

If either is unclear, ask: "What should the feature be called, and what should it display?"

---

## Prerequisites

The templates below use libraries the template does not have yet. Before the first screen, check `gradle/libs.versions.toml` and the feature module's `build.gradle.kts`, and add what is missing:

- `androidx.lifecycle:lifecycle-viewmodel-compose` and `lifecycle-runtime-compose` (ViewModel, `collectAsStateWithLifecycle`)
- `androidx.navigation:navigation-compose` and `kotlinx-serialization-json` with the serialization plugin (typed routes), if the screen is navigated to
- Hilt: not yet. Construct the ViewModel with `viewModel { ... }` until it is added.

---

## Steps

### 1. Choose the UI states

Match the real requirements; do not apply one template to every screen.

- Loading: `isLoading = true`
- Content: `isLoading = false`, data present
- Error: `error != null`
- Empty: `isLoading = false`, empty list, no error

### 2. Create the package

```
feature/<feature>/src/main/java/com/moisesai/challange/feature/<feature>/presentation/
├── composable/<Feature>Screen.kt
├── model/Ui<Model>.kt
├── view_model/<Feature>ViewModel.kt
├── navigation/<Feature>Navigation.kt
└── mapper/<Model>Mapper.kt        # only if a domain model needs UI mapping
```

Never put a feature's files flat in the module root, and never import another feature's package: share through `core:*` or the `:app` NavHost.

### 3. UiModel

`model/Ui<Model>.kt`: a plain immutable data class, prefix `Ui`, no Android or Compose imports.

```kotlin
package com.moisesai.challange.feature.<feature>.presentation.model

data class Ui<Model>(
    val id: String,
    val title: String,
    // … other display fields
)
```

### 4. Mapper (optional)

```kotlin
package com.moisesai.challange.feature.<feature>.presentation.mapper

import com.moisesai.challange.feature.<feature>.presentation.model.Ui<Model>
import com.moisesai.challange.core.sharedmodel.<DomainModel>DomainModel // or feature.<feature>.domain.model for a feature-only model

fun <DomainModel>DomainModel.toUiModel(): Ui<Model> = Ui<Model>(
    id = id,
    title = title,
    // … map remaining fields
)
```

Field mapping and simple formatting only. Called from the ViewModel, never from a composable.

### 5. ViewModel

`UiState` sits at the top of the same file.

```kotlin
package com.moisesai.challange.feature.<feature>.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moisesai.challange.feature.<feature>.presentation.model.Ui<Model>
import com.moisesai.challange.feature.<feature>.domain.usecase.<UseCase>
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class <Feature>UiState(
    val items: List<Ui<Model>> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class <Feature>ViewModel(
    private val useCase: <UseCase>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(<Feature>UiState())
    val uiState: StateFlow<<Feature>UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            useCase()
                .onSuccess { data ->
                    _uiState.update { it.copy(items = data.map { item -> item.toUiModel() }, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
```

For a use case that returns `Flow<Result<T>>` (a stream, e.g. a Room query), collect it and fold each emission the same way. The Flow never throws, so no `.catch` is needed here:

```kotlin
private var observeJob: Job? = null

fun observe() {
    observeJob?.cancel()
    observeJob = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        useCase().collect { result ->
            result
                .onSuccess { data ->
                    _uiState.update { it.copy(items = data.map { item -> item.toUiModel() }, isLoading = false, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
```

Use suspend by default; use the Flow form only when the use case really is a stream (`ARCHITECTURE.md` §2, "Suspend or Flow?"). Domain -> `Ui*` mapping happens here in both forms.

Rules:
- One `StateFlow<UiState>`; `isLoading` and `error` are mandatory
- Business logic stays out of composables; UI logic only in the ViewModel
- If it later needs Hilt, add `@HiltViewModel` and `@Inject constructor`

### 6. Reusable components

Before writing the screen, list the components it needs (cards, rows, banners, empty states). For each: check `core/design/components/` for a match; if it will be reused across features, create it with `create-ui-component`; if it is specific to this feature, keep it in this feature's `presentation/composable/` package (private if used in one file).

### 7. Screen composable

`composable/<Feature>Screen.kt` is **stateless**: it receives `uiState` and callbacks.

```kotlin
package com.moisesai.challange.feature.<feature>.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.feature.<feature>.presentation.model.Ui<Model>
import com.moisesai.challange.feature.<feature>.presentation.view_model.<Feature>UiState
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme

@Composable
fun <Feature>Screen(
    uiState: <Feature>UiState,
    onSomeAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            else -> <Feature>Content(uiState = uiState, onSomeAction = onSomeAction)
        }
    }
}

@Composable
private fun <Feature>Content(
    uiState: <Feature>UiState,
    onSomeAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // TODO: build content
    }
}
```

Rules:
- `Scaffold` when the screen needs a toolbar, FAB or snackbar
- All user-facing text from `R.string.*` (`app/src/main/res/values/strings.xml`)
- Colours, typography and shapes only from `MaterialTheme`; no hex literals, no inline `TextStyle`
- `modifier: Modifier = Modifier` is the first optional parameter

### 8. Navigation graph

Needs the navigation dependencies from Prerequisites.

```kotlin
package com.moisesai.challange.feature.<feature>.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.feature.<feature>.presentation.composable.<Feature>Screen
import com.moisesai.challange.feature.<feature>.presentation.view_model.<Feature>ViewModel
import kotlinx.serialization.Serializable

@Serializable
object <Feature>Route

fun NavGraphBuilder.<feature>Graph(
    onSomeAction: () -> Unit,
) {
    composable<<Feature>Route> {
        val viewModel: <Feature>ViewModel = viewModel { <Feature>ViewModel(/* use cases */) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        <Feature>Screen(uiState = uiState, onSomeAction = onSomeAction)
    }
}
```

Register `<feature>Graph(...)` in the root `NavHost` in `:app`; navigation callbacks are lambdas supplied there, and routes carry ids only. `hiltViewModel()` replaces `viewModel { ... }` after Hilt is added.

### 9. Previews

Every screen file has `@FullPreview` previews, each wrapped in `MoisesaiChallangeTheme { }`. At minimum: content, loading, error.

```kotlin
@FullPreview
@Composable
private fun <Feature>ContentPreview() {
    MoisesaiChallangeTheme {
        <Feature>Screen(
            uiState = <Feature>UiState(
                isLoading = false,
                items = listOf(Ui<Model>(id = "1", title = "Sample")),
            ),
            onSomeAction = {},
        )
    }
}

@FullPreview
@Composable
private fun <Feature>LoadingPreview() {
    MoisesaiChallangeTheme {
        <Feature>Screen(uiState = <Feature>UiState(isLoading = true), onSomeAction = {})
    }
}

@FullPreview
@Composable
private fun <Feature>ErrorPreview() {
    MoisesaiChallangeTheme {
        <Feature>Screen(
            uiState = <Feature>UiState(isLoading = false, error = "Something went wrong"),
            onSomeAction = {},
        )
    }
}
```

### 10. Output summary

List every file created under `feature/<feature>/` with its purpose, and remind the user to:

- register the graph in the root `NavHost` (if one exists)
- write ViewModel unit tests (JUnit 4 + MockK + coroutines-test)
- add any dependency listed in Prerequisites that was missing

---

## Hard Constraints

- No Activity or Fragment per screen
- The screen composable is stateless; state collection lives in the navigation graph
- A ViewModel calls use cases only
- Composables never collect a domain Flow or map models; only the `StateFlow<UiState>` reaches the navigation graph
- Every screen composable has `@FullPreview` previews wrapped in `MoisesaiChallangeTheme { }`
- A feature's UI files are nested under `feature/<feature>/presentation/{composable,model,view_model,navigation,mapper}`
- Presentation never imports the feature's `data` package; a feature never imports another feature
- State shared between features goes through `core:*` (shared model or Room table), never across feature modules
