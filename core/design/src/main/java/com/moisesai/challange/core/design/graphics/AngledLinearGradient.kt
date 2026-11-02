package com.moisesai.challange.core.design.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** The two points a linear gradient runs between. */
internal data class GradientLine(val start: Offset, val end: Offset)

/**
 * Where a CSS `linear-gradient(<angle>, ...)` starts and ends in a box. In CSS the angle is measured clockwise from
 * "up", and the line is long enough that the box corners get exactly the first and last colour. Compose's own
 * gradient goes from corner to corner, which is a different line for every angle except 135 degrees on a square.
 */
internal fun cssGradientLine(angleDegrees: Float, size: Size): GradientLine {
    val angle = Math.toRadians(angleDegrees.toDouble())
    val directionX = sin(angle).toFloat()
    val directionY = -cos(angle).toFloat()
    // The gradient line is as long as the box is when projected onto the direction of the gradient.
    val halfLength = (abs(size.width * directionX) + abs(size.height * directionY)) / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    return GradientLine(
        start = center - Offset(directionX, directionY) * halfLength,
        end = center + Offset(directionX, directionY) * halfLength,
    )
}

/** A brush that paints a CSS-style angled linear gradient, whatever size the shape it paints turns out to be. */
fun angledLinearGradient(angleDegrees: Float, vararg colorStops: Pair<Float, Color>): Brush =
    angledLinearGradientOver(angleDegrees, colorStops.toList()) { size -> Rect(Offset.Zero, size) }

/**
 * Like [angledLinearGradient], but the gradient is laid over [box] instead of over the shape being painted. That is
 * what CSS does when text is filled with a gradient: the gradient covers the text's inline box, which can be taller
 * than the line that is drawn. [box] is given the size of what is painted and answers where the gradient lives.
 */
fun angledLinearGradientOver(
    angleDegrees: Float,
    colorStops: List<Pair<Float, Color>>,
    box: (Size) -> Rect,
): Brush {
    val positions = colorStops.map { it.first }
    val colors = colorStops.map { it.second }
    return object : ShaderBrush() {
        override fun createShader(size: Size): android.graphics.Shader {
            val area = box(size)
            val line = cssGradientLine(angleDegrees, area.size)
            return LinearGradientShader(line.start + area.topLeft, line.end + area.topLeft, colors, positions)
        }
    }
}
