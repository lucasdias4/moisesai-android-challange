package com.moisesai.challange.core.design.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/** How a glass surface looks: its [shape], the [fill] laid over the blur, and how strong the [blur] is. */
@Immutable
internal class UiGlassStyle(
    val shape: Shape,
    val fill: Color,
    val blur: Dp,
)
