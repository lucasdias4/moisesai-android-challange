package com.moisesai.challange.core.design.animation

import androidx.compose.animation.core.CubicBezierEasing

// The curves of the agreed design, written as the same four numbers its CSS uses so the motion matches exactly.

/** Overshoots the end value a little and settles back: the springy arrival of cards, rows and chips. */
val SpringOvershootEasing = CubicBezierEasing(0.2f, 1.5f, 0.35f, 1f)

/** Fast start and a very long, soft finish: the slow colour change of the background. */
val AmbientEasing = CubicBezierEasing(0.2f, 0.9f, 0.2f, 1f)

/** CSS `ease`: the default curve for plain fades. */
val CssEaseEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

/** CSS `ease-in-out`: symmetric, for effects that go back and forth, like the equalizer bars. */
val CssEaseInOutEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
