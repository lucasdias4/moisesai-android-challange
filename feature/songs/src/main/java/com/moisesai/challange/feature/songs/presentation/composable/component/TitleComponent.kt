package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.graphics.angledLinearGradientOver
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.text.lineBox
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.TitleSize

// In the design the dot is filled with a gradient that covers the text's inline box. That box is taller than the
// line, 70 dp for this size, and starts 7.45 dp above the line, and the gradient is laid over all of it.
private val DotGradientBoxHeight = 70.dp
private val DotGradientBoxTop = (-7.45).dp
private const val DOT_GRADIENT_ANGLE_DEGREES = 135f

/** The name of the app. The last character, the dot, is lime to magenta when big and plain lime when compact. */
@Composable
internal fun TitleComponent(size: TitleSize, modifier: Modifier = Modifier) {
    val text = MaterialTheme.aurora.text
    val colors = MaterialTheme.colorScheme
    val title = stringResource(R.string.songs_title)
    val style = if (size == TitleSize.Big) text.title else text.compactTitle
    val dotBrush = rememberDotBrush(size)
    Row(modifier = modifier.lineBox(style)) {
        Text(text = title.dropLast(1), style = style, color = colors.onBackground, maxLines = 1)
        Text(
            text = title.takeLast(1),
            style = if (dotBrush == null) style.copy(color = colors.primary) else style.withBrush(dotBrush),
            maxLines = 1,
        )
    }
}

private fun TextStyle.withBrush(brush: Brush) = copy(brush = brush)

@Composable
private fun rememberDotBrush(size: TitleSize): Brush? {
    if (size == TitleSize.Compact) return null
    val colors = MaterialTheme.colorScheme
    val density = LocalDensity.current
    return remember(colors.primary, colors.secondary, density) {
        val boxHeight = with(density) { DotGradientBoxHeight.toPx() }
        val boxTop = with(density) { DotGradientBoxTop.toPx() }
        angledLinearGradientOver(
            DOT_GRADIENT_ANGLE_DEGREES,
            listOf(0f to colors.primary, 1f to colors.secondary),
        ) { painted -> Rect(0f, boxTop, painted.width, boxTop + boxHeight) }
    }
}

@FullPreview
@Composable
private fun TitleBigPreview() = PreviewSurface { TitleComponent(size = TitleSize.Big) }

@FullPreview
@Composable
private fun TitleCompactPreview() = PreviewSurface { TitleComponent(size = TitleSize.Compact) }
