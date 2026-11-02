package com.moisesai.challange.core.design.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** One CSS shadow: `box-shadow: 0 <offsetY> <blur> <color>`. */
@Immutable
class UiBoxShadow(
    val color: Color,
    val blur: Dp,
    val offsetY: Dp,
)
