package com.moisesai.challange.feature.songs.presentation.composable.animation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

// The caret is on for half a second and off for half a second. The change is a jump, not a fade, like a real caret.
private const val BLINK_MILLIS = 1_000
private const val BLINK_HALF_MILLIS = BLINK_MILLIS / 2

/** 1 while the caret is shown and 0 while it is hidden. Read while drawing, so blinking never recomposes. */
@Composable
internal fun rememberCaretBlinkAlpha(): State<Float> =
    // The value starts and ends at 1 because the keyframes below describe the whole cycle: on, jump to off, jump back.
    rememberInfiniteTransition(label = "caretBlink").animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = BLINK_MILLIS
                1f at 0
                // The jump takes one millisecond: as close to instant as a keyframe animation can be.
                1f at BLINK_HALF_MILLIS - 1
                0f at BLINK_HALF_MILLIS
                0f at BLINK_MILLIS
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "caretBlinkAlpha",
    )
