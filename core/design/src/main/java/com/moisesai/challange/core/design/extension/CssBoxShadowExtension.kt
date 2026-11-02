package com.moisesai.challange.core.design.extension

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.moisesai.challange.core.design.model.UiBoxShadow

// A CSS shadow blurs with a gaussian whose standard deviation is half the blur value. Android's blur mask filter
// has a standard deviation of 0.57735 * radius + 0.5, so the radius to ask Android for is derived from the CSS value.
private const val BLUR_SIGMA_PER_RADIUS = 0.57735f
private const val BLUR_SIGMA_OFFSET = 0.5f

/** The radius to give Android's blur so it blurs like a CSS shadow of [cssBlurPx] pixels. */
internal fun cssBlurToBlurRadius(cssBlurPx: Float): Float =
    if (cssBlurPx <= 0f) 0f else ((cssBlurPx / 2f) - BLUR_SIGMA_OFFSET) / BLUR_SIGMA_PER_RADIUS

/**
 * CSS box shadows behind the content, first one on top, as in CSS. [shadows] is read while drawing, so shadows that
 * change every frame (a glow growing as a field is focused) never recompose anything. Like in CSS the shadows are
 * only painted outside the shape, so a translucent surface does not show its own shadow through itself.
 */
fun Modifier.cssBoxShadows(shape: Shape, shadows: () -> List<UiBoxShadow>): Modifier = drawBehind {
    val shapePath = shape.createOutline(size, layoutDirection, this).toPath()
    clipPath(shapePath, clipOp = ClipOp.Difference) {
        shadows().asReversed().forEach { shadow -> drawBoxShadow(shapePath, shadow) }
    }
}

private fun DrawScope.drawBoxShadow(shapePath: Path, shadow: UiBoxShadow) {
    val blurRadius = cssBlurToBlurRadius(shadow.blur.toPx())
    val paint = Paint().apply {
        isAntiAlias = true
        color = shadow.color.toArgb()
        if (blurRadius > 0f) maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }
    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(0f, shadow.offsetY.toPx())
        canvas.nativeCanvas.drawPath(shapePath.asAndroidPath(), paint)
        canvas.restore()
    }
}

private fun Outline.toPath(): Path = Path().also { path ->
    when (this) {
        is Outline.Rectangle -> path.addRect(rect)
        is Outline.Rounded -> path.addRoundRect(roundRect)
        is Outline.Generic -> path.addPath(this.path)
    }
}
