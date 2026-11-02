package com.moisesai.challange.core.design.ambient

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val CHANNEL_MAX = 255

// The background is lit by the artwork's colour. Dark covers would give an almost black screen, so the colour is
// scaled up until its strongest channel reaches this level, but never by more than the limit, so grey covers stay calm.
private const val TARGET_PEAK = 215f
private const val MAX_BOOST = 2.6f

/** The colour of the ambient light for an artwork whose average colour is [average]. */
fun ambientTintOf(average: Color): Color {
    val red = average.red * CHANNEL_MAX
    val green = average.green * CHANNEL_MAX
    val blue = average.blue * CHANNEL_MAX
    // At least 1, so a black cover does not divide by zero.
    val boost = min(MAX_BOOST, TARGET_PEAK / max(max(red, green), max(blue, 1f)))
    return Color(
        red = scaled(red, boost),
        green = scaled(green, boost),
        blue = scaled(blue, boost),
    )
}

private fun scaled(channel: Float, boost: Float): Int = min(CHANNEL_MAX, (channel * boost).roundToInt())

/** The plain arithmetic mean of the pixels' colours (ARGB ints), ignoring transparency. */
fun averageColorOf(pixels: IntArray): Color {
    require(pixels.isNotEmpty()) { "There are no pixels to average." }
    var red = 0L
    var green = 0L
    var blue = 0L
    pixels.forEach { pixel ->
        red += (pixel shr 16) and 0xFF
        green += (pixel shr 8) and 0xFF
        blue += pixel and 0xFF
    }
    val count = pixels.size
    return Color(
        red = (red.toDouble() / count).roundToInt(),
        green = (green.toDouble() / count).roundToInt(),
        blue = (blue.toDouble() / count).roundToInt(),
    )
}
