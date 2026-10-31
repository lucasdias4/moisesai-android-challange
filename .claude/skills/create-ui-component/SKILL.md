---
name: create-ui-component
description: Create a reusable Moisesai Challange Compose component using the project theme (`MaterialTheme`, `ui/theme/`). Use when building a shared composable - a card, button variant, list item, badge, dialog, bottom sheet, etc.
---

Create a reusable Compose component in `app/src/main/java/com/moisesai/challange/ui/components/`, themed only through `MaterialTheme`.

---

## When to Use

- A shared composable needed by more than one screen
- Extending or wrapping an existing component

## When NOT to Use

- UI used by a single feature: keep it in `<feature>/composable/`
- A whole screen: use `create-compose-screen`
- Business logic: composables are UI only

---

## Input

- Component name
- Purpose
- States or variants it must represent

---

## Steps

### 1. Decide ownership

- Reused by several features: `ui/components/`
- Used by one feature: that feature's `composable/` package

### 2. Check for existing components

Search `ui/components/` first. Extend or wrap a close match. If variants differ only by spacing, alignment, icon placement or colour, make one configurable composable instead of two.

### 3. Define the API

```kotlin
@Composable
fun ComponentName(
    // Required parameters first
    requiredValue: Type,

    // Modifier is the first optional parameter
    modifier: Modifier = Modifier,

    // Remaining optional parameters
    optionalValue: Type = Default,

    // Callbacks last
    onClick: (() -> Unit)? = null,
)
```

Prefer an enum or sealed type over boolean flags that select a variant.

### 4. Theme it

Colours from `MaterialTheme.colorScheme`:

```kotlin
Surface(color = MaterialTheme.colorScheme.surface) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
    )
}
```

- Typography: `MaterialTheme.typography`
- Shapes: `MaterialTheme.shapes`, never hardcoded corner radii
- If the palette needs a new colour, add it in `ui/theme/Color.kt` and `Theme.kt`, not as a literal at the call site
- Dark mode and dynamic colour come from `MoisesaiChallangeTheme`; read `isSystemInDarkTheme()` only inside the theme

### 5. Previews

Every component file has its own `@FullPreview` previews, wrapped in `MoisesaiChallangeTheme { }`: the default state plus one per visually distinct state (disabled, loading, error, empty, selected). `@FullPreview` is `com.moisesai.challange.ui.preview.FullPreview`.

```kotlin
import com.moisesai.challange.ui.preview.FullPreview

@FullPreview
@Composable
private fun ComponentNamePreview() {
    MoisesaiChallangeTheme {
        ComponentName(
            // sample data
        )
    }
}

@FullPreview
@Composable
private fun ComponentNameDisabledPreview() {
    MoisesaiChallangeTheme {
        ComponentName(
            enabled = false,
        )
    }
}
```

### 6. Report

- Created file path and import path (`com.moisesai.challange.ui.components.*`)
- Public composable names
- Theme tokens used
- Existing components extended or wrapped
- A reminder to add Compose UI tests if the component has conditional logic

---

## Hard Constraints

- Expose callbacks instead of performing actions
- No business logic in composables
- Every public composable has `@FullPreview` previews in the same file
- Never hardcode colours, typography or shapes when a theme token exists
- `modifier: Modifier = Modifier` is the first optional parameter
- Prefer extending an existing component over duplicating it
