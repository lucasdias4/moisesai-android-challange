---
name: create-ui-component
description: Create a reusable Moisesai Challange Compose component using the project theme (`MaterialTheme`, `core/design/theme/`). Use when building a shared composable - a card, button variant, list item, badge, dialog, bottom sheet, etc.
---

Create a reusable Compose component in `:core:design` (`core/design/src/main/java/com/moisesai/challange/core/design/components/`), themed only through `MaterialTheme`.

---

## When to Use

- A shared composable needed by more than one screen
- Extending or wrapping an existing component

## When NOT to Use

- UI used by a single feature: keep it in that feature's `presentation/composable/`
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

- Reused by several features: `core/design/components/` (features may not share components by importing each other)
- Used by one feature: that feature's `presentation/composable/` package

### 2. Check for existing components

Search `core/design/components/` first. Extend or wrap a close match. If variants differ only by spacing, alignment, icon placement or colour, make one configurable composable instead of two.

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
- A component whose job is an animation is not a shared component: use the `create-animation` skill (`animation` package next to its screen, name and file end in `Animation`)
- Shapes: `MaterialTheme.shapes`, never hardcoded corner radii
- If the palette needs a new colour, add it in `core/design/theme/Color.kt` and `Theme.kt`, not as a literal at the call site
- The Aurora Neon palette and typeface come from `MoisesaiChallangeTheme` (dark only, no dynamic colour); never read `isSystemInDarkTheme()` in a component

### 5. Previews

Every component file has its own `@FullPreview` previews, wrapped in `MoisesaiChallangeTheme { }`: the default state plus one per visually distinct state (disabled, loading, error, empty, selected). `@FullPreview` is `com.moisesai.challange.core.design.preview.FullPreview`.

```kotlin
import com.moisesai.challange.core.design.preview.FullPreview

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

### 6. Clean code check

Re-read the component against `ARCHITECTURE.md` §8:

- The name says what it shows (a noun), and no parameter is a boolean that switches between two looks.
- Few parameters (at most three beyond `modifier`), the rest grouped or defaulted deliberately.
- No magic numbers: spacing and sizes are named constants or theme values.
- Small: split anything longer than one screen of code, and put each preview next to what it previews.
- No unused parameters, imports or commented-out code.

For a full review, use the `clean-code-review` skill.

### 7. Report

- Created file path and import path (`com.moisesai.challange.core.design.components.*`)
- Public composable names
- Theme tokens used
- Existing components extended or wrapped
- A reminder to add Compose UI tests if the component has conditional logic

---

## Hard Constraints

- Expose callbacks instead of performing actions
- No business logic in composables
- Every component's name and file end in `Component` (`SearchFieldComponent.kt`); in a feature it lives in `presentation/composable/component`
- Every component, private parts included, has `@FullPreview` previews in the same file, on the feature's `PreviewSurface` and sample data (`presentation/composable/component/preview_helper`)
- Never hardcode colours, typography or shapes when a theme token exists
- `modifier: Modifier = Modifier` is the first optional parameter
- Prefer extending an existing component over duplicating it
