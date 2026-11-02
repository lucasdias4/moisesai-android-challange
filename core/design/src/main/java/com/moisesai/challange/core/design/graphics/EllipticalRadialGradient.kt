package com.moisesai.challange.core.design.graphics

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush

/**
 * A CSS `radial-gradient(<rx> <ry> at <center>, ...)`: an ellipse, not a circle. Compose only has circular radial
 * gradients, so a circle of radius [horizontalRadius] is squashed vertically to [verticalRadius] around its centre.
 * [center], [horizontalRadius] and [verticalRadius] are given for the size being painted.
 */
fun ellipticalRadialGradient(
    center: (Size) -> Offset,
    horizontalRadius: (Size) -> Float,
    verticalRadius: (Size) -> Float,
    vararg colorStops: Pair<Float, Color>,
): Brush {
    val positions = colorStops.map { it.first }
    val colors = colorStops.map { it.second }
    return object : ShaderBrush() {
        override fun createShader(size: Size): android.graphics.Shader {
            val middle = center(size)
            val radiusX = horizontalRadius(size)
            val shader = RadialGradientShader(middle, radiusX, colors, positions)
            // The gradient stays a circle in the shader; the matrix is what turns it into the ellipse.
            val squash = Matrix().apply { setScale(1f, verticalRadius(size) / radiusX, middle.x, middle.y) }
            shader.setLocalMatrix(squash)
            return shader
        }
    }
}
