---
name: create-animation
description: Create or change a Moisesai Challange Compose animation - an `animation` package next to its screen, one file per animation with the `Animation` suffix, a single `Phase` timeline, commented values, draw-phase state reads, accessibility and a clock-driven test. Use when a screen needs motion (splash, loading, transitions, micro-interactions) or when reviewing an animation.
---

Build motion following `ARCHITECTURE.md` §4 "Animations". The app's pace (300 ms, easing out) and its shared animations are in `core/design/animation/` (`spec/ScreenSpec.kt`, `ShimmerAnimation`, `PressAnimation`, `SheetAnimation`, ...): read them before writing a new one, and reuse `screenTween()` for any big change. An animation that only one screen uses goes in that screen's `animation` package, as the steps below say. Real examples: `splash/animation` (`SplashPhase`, `NoteAnimation`) and `songdetails/presentation/composable/animation`.

---

## When to Use

- A screen needs a sequence (splash, onboarding, empty-state intro) or a continuous effect (equalizer, glow, shimmer)
- Adding or changing a file in an `animation` package
- Reviewing animation code

## When NOT to Use

- A plain state change a Material component already animates (`AnimatedVisibility`, `animateContentSize`): use it directly in the screen
- A shared, static component: use `create-ui-component`

---

## Input

- Which screen owns the animation
- The steps in order and roughly how long each lasts
- Which colours, sizes and text it needs

---

## Steps

### 0. Reuse what exists

Look at this table before writing anything. All of these are in `core/design/animation` (the pace is in `animation/spec/ScreenSpec.kt`):

| The screen needs | Use |
|---|---|
| The pace of a big change (300 ms, easing out) | `screenTween()` |
| A placeholder while something loads | `SkeletonBlockComponent`, `SkeletonTextComponent` (they use `shimmerAnimation`) |
| A cover picture | `CoverImageComponent` (shimmer, fade in, small picture as placeholder) |
| Press feedback on a row or a button | `pressAnimation` instead of `clickable` |
| Items of a list that appear, go away or move | `itemAnimation` (the list needs a key per item) |
| A skeleton that becomes the real screen | `StateAnimation` |
| A field or a block that opens and closes | `CollapseAnimation`; a small button that appears: `PopAnimation` |
| A sheet over the screen | `SheetAnimation` |
| A cover that flies from one screen to the next | `SharedCoverAnimation` |

Write a new animation only when none of these fits.

### 1. Place it

```text
<screen>/
├── <Screen>Screen.kt
└── animation/
    ├── <Screen>Phase.kt
    ├── <Name>Animation.kt          one animation per file
    └── ...
```

- The package is called `animation` and sits next to the screen (`splash/animation`). The screen imports from it; it never imports the screen.
- Every composable that is an animation, and its file, ends in `Animation`: `BarsAnimation`, `GlowAnimation`, `TitleAnimation`. A Modifier that animates follows it too (`Modifier.splashExitAnimation(phase)` in `SplashExitAnimation.kt`).
- Everything is `internal`. The `<Screen>Phase` enum lives in this package, because the animations depend on it.

### 2. Write one timeline

If the animation has steps, describe them once as an enum with each step's duration, in order:

```kotlin
internal enum class SplashPhase(val durationMillis: Long) {
    Equalizing(durationMillis = 1_800),
    Collapsing(durationMillis = 600),
    Revealing(durationMillis = 2_500),
    Exiting(durationMillis = 900),
}
```

The screen walks it with one `LaunchedEffect`, and finishes with a callback:

```kotlin
LaunchedEffect(Unit) {
    SplashPhase.entries.forEach { nextPhase ->
        phase = nextPhase
        delay(nextPhase.durationMillis)
    }
    currentOnFinished() // rememberUpdatedState, so a new lambda is still called
}
```

Animations are stateless: they take `phase` and react (`phase >= SplashPhase.Collapsing` means "that step or any later one"). Do not give an animation its own timer for the whole sequence.

### 3. Write each animation

```kotlin
private const val ENTRY_MILLIS = 1_400
private const val ENTRY_START_SCALE = 0.6f

@Composable
internal fun GlowAnimation(
    phase: SplashPhase,
    modifier: Modifier = Modifier,
) {
    // 0 = hidden, 1 = fully shown. Starts at Collapsing so the glow is ready when the title arrives.
    val entryProgress by animateFloatAsState(
        targetValue = if (phase >= SplashPhase.Collapsing) 1f else 0f,
        animationSpec = tween(ENTRY_MILLIS, easing = FastOutSlowInEasing),
        label = "glowEntryProgress",
    )
    ...
}
```

- Signature: `phase` (or a `progress`), then `modifier: Modifier = Modifier` as the first optional parameter.
- Name every `label` so the animation shows up in the Compose tooling.
- Colours from `MaterialTheme.colorScheme`, text from `:core:strings` (`stringResource`), durations, sizes and ratios as named constants (`private const val` or a `private val ... = 48.dp`). No hex colours, no magic numbers.

### 4. Comment it so it can be read in a minute

Short, direct sentences. Say what the value does in the picture and why it is that way; never repeat the code. Every one of these gets a comment:

| What | Comment says | Example |
|---|---|---|
| A constant | What it controls, and the unit or effect | `// Bars bounce at 4 different speeds (420 to 750 ms), so the row looks random and not in sync.` |
| An animated state | Its range and what each end means | `// 0 = bars bouncing, 1 = bars collapsed into dots.` |
| A non-obvious call | The reason | `// Fast-forwarding makes each bar start at a different point, so none begins flat.` |
| A trick (clamp, overshoot, stagger) | What would break without it | `// The bouncy spring overshoots 1, so alpha is clamped.` |
| An accessibility choice | What the reader hears | `// Screen readers read the name once, not letter by letter.` |

The top of every animation file is **two lines of KDoc**: line 1 says what it does in one plain sentence; line 2 says where it is used and how, with a real call.

```kotlin
/**
 * Grows its content from a smaller size while it fades in, and shrinks it while it fades out.
 * Used in `HeaderSection`, on the search magnifier: `PopAnimation(isVisible = isSearchButtonVisible) { SearchButton(onClick = onSearchClick) }`.
 */
```

**Each place that uses an animation** gets a one-line comment saying what it does there, and which animation when that is not obvious. Only animations, not the lines around them:

```kotlin
// Shared animation: the cover flies between the album track row and the player the track was opened in.
CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) { ... }
```

The phase enum's KDoc lists the steps. Use plain words in names and comments: a reader should not need a dictionary.

### 5. Keep frames cheap

- Read animated values in `graphicsLayer { }` or in a draw block (`Canvas`, `drawBehind`), not in composition. A frame then redraws and does not recompose:

```kotlin
.graphicsLayer { alpha = fadeOut }          // value read while drawing
Canvas(modifier) { drawBars(bounceFractions, collapseFraction, brush) }
```

- Many identical shapes: one `Canvas`, not one composable per shape.
- A Modifier that animates is a `Modifier.Node` (`ModifierNodeElement` with `DrawModifierNode`; see `ShimmerAnimation` and `PressAnimation`), not a `@Composable` or `composed` factory. It builds what it draws (a brush) once and only moves it each frame.
- Animate `alpha`, scale, rotation and translation. Avoid animating layout size inside a list or a frame loop.
- Endless effects use `rememberInfiniteTransition`. A fixed number of them in a loop is safe; a changing number is not.
- Remember brushes and shadows with the theme colours as keys, so they are not rebuilt every frame.

### 6. Accessibility

- A decorative animation adds no semantics. Skeleton blocks are decoration: they add none.
- Text animated piece by piece is read once: put `clearAndSetSemantics { contentDescription = text }` on the container (see `TitleAnimation`).
- Compose follows the system animator scale, so endless animations stop when the user turns animations off. Do not work around it.

### 7. Wire it into the screen

The screen stays small: it owns the phase, stacks the animations and applies the exit.

```kotlin
Box(modifier = Modifier.splashExitAnimation(phase), contentAlignment = Alignment.Center) {
    GlowAnimation(phase = phase)
    BarsAnimation(phase = phase)
    TitleAnimation(phase = phase)
}
```

A shared element (`SharedCoverAnimation`) can only fly to a screen that already shows its target: when the data is in the cache, show it at once and let the extras load after, instead of a skeleton first.

The screen has a `@FullPreview` wrapped in `MoisesaiChallangeTheme`. The caller decides what happens at the end (`onFinished`); the animation package knows nothing about navigation.

### 8. Test the timeline

An instrumented test (names with underscores, `GIVEN_..._WHEN_..._THEN_...`) drives the clock by hand, because an endless animation never lets the UI become idle:

```kotlin
composeTestRule.mainClock.autoAdvance = false
composeTestRule.setContent { MoisesaiChallangeTheme { SplashScreen(onFinished = onFinished) } }
composeTestRule.mainClock.advanceTimeBy(SplashPhase.entries.sumOf { it.durationMillis } + 1_000)
```

Cover: it does not finish halfway, it finishes once after the last phase, and the final text is shown (read from `:core:strings`, not hardcoded). Do not assert pixels. Stop before the exit phase when a test needs the content to be visible.

### 9. See it running

Tests do not show motion, so look at it on the emulator before calling it done:

- Record it (`adb shell screenrecord`), pull the file and cut frames with `ffmpeg` into one contact sheet, so you see the steps and not only the end.
- For a loading state, slow the network (`adb emu network speed gsm` and `network delay gprs`) and use a query that is not in the cache.
- Turn system animations off (`animator_duration_scale`, `transition_animation_scale` and `window_animation_scale` to 0): everything must still work, only without motion.
- Compare the frame stats (`adb shell dumpsys gfxinfo <package>`) with the version before: the animation must not cost clearly more than what it replaces. The emulator renders in software, so compare the two, never read the absolute number.
- Rotate while it runs, and turn the network off.

### 10. Clean code check

Run `clean-code-review`, plus:

- Does every file end in `Animation` and hold one animation?
- Does each file start with the two-line KDoc (what it does; where it is used and how), and does each use have a one-line comment?
- Are the names plain words, and did it reuse a shared animation when one fits?
- Is every constant, animated value and non-obvious call commented with meaning and reason?
- Is animated state read in a draw or `graphicsLayer` block?
- Any hex colour, hardcoded text or unnamed number left?
- Does the screen package depend on `animation`, and not the other way round?

### 11. Report

- Package and files created, with the phases and their durations
- What starts and ends the sequence (the callback)
- The test written and the preview added

---

## Hard Constraints

- Animations live in an `animation` package next to the screen, one per file, name and file ending in `Animation`
- One `Phase` enum is the only timeline; animations are stateless
- Every constant, animated value and non-obvious call has a short, direct comment (meaning and reason)
- Each animation file starts with a two-line KDoc (what it does; where it is used and how), and each use has a one-line comment
- Reuse the shared animations of `core/design/animation` before writing a new one
- Animated values are read in draw or `graphicsLayer`, never in composition
- Colours from `MaterialTheme`, text from `:core:strings`, no magic numbers
- Animated text has one `contentDescription`
- A clock-driven test covers the timeline
