package com.moisesai.challange.splash.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val SpotlightStart = Color(0xFF000000)
private val SpotlightEnd = Color(0xFF0086A0)

// The design draws the gradient (`Base/Gradients/Spotlight`) from a point left of the middle and low, to a point far
// above and beyond the right edge, so the teal only reaches the top right corner. Each pair is a share of the width
// and of the height of what it is drawn on.
private const val START_X = 0.3756f
private const val START_Y = 0.6971f
private const val END_X = 1.8679f
private const val END_Y = -1.2019f

/** Paints the spotlight of the design: black that turns teal towards the top right corner. */
internal fun Modifier.spotlightBackground(): Modifier = drawBehind {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(SpotlightStart, SpotlightEnd),
            start = Offset(size.width * START_X, size.height * START_Y),
            end = Offset(size.width * END_X, size.height * END_Y),
        ),
    )
}
