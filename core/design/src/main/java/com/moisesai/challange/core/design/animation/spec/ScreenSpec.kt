package com.moisesai.challange.core.design.animation.spec

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween

/**
 * How long a big change takes: 300 ms, the time the design takes to go from one thing to the next.
 * Used by every animation of a screen, sheet or picture: `crossfade(SCREEN_MILLIS)` in `CoverImageComponent`, and `screenTween()` below in `ScreenTransitionAnimation`.
 */
const val SCREEN_MILLIS = 300

// Fast at the start, soft at the end: the new thing arrives at once and settles without a jolt.
val ScreenEasing: Easing = EaseOut

// Both together, the way the animations receive them: `fadeIn(screenTween())`.
fun <T> screenTween(): TweenSpec<T> = tween(durationMillis = SCREEN_MILLIS, easing = ScreenEasing)
