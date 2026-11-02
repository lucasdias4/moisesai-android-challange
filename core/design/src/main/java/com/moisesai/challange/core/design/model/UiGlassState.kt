package com.moisesai.challange.core.design.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

/**
 * The place where glass finds what is behind it. It wraps the blur library, so the rest of the app never sees it
 * and the way the blur is made can change without touching a screen.
 */
@Stable
class UiGlassState internal constructor(internal val blur: HazeState)

/** Remembers the place where glass finds what is behind it. Give it to `glassSource` and to every glass surface. */
@Composable
fun rememberUiGlassState(): UiGlassState {
    val blur = rememberHazeState()
    return remember(blur) { UiGlassState(blur) }
}
