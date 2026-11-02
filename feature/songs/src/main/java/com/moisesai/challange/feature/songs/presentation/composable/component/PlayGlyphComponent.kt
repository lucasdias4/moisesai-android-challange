package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

// The play triangle of the design, as drawn inside its 46 dp button: it is slightly left of centre so that it looks
// centred (a triangle's weight is on its flat side), 10.25 dp wide and 11.75 dp tall.
private val ButtonSize = 46.dp
private const val LEFT = 18f
private const val TOP = 16.5f
private const val TIP_X = 28.25f
private const val MIDDLE_Y = 22.375f
private const val BOTTOM = 28.25f

/** The play triangle, laid out for a 46 dp button. */
@Composable
internal fun PlayGlyphComponent(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(ButtonSize)) {
        val unit = size.width / ButtonSize.value
        val triangle = Path().apply {
            moveTo(LEFT * unit, TOP * unit)
            lineTo(TIP_X * unit, MIDDLE_Y * unit)
            lineTo(LEFT * unit, BOTTOM * unit)
            close()
        }
        drawPath(triangle, color)
    }
}

@FullPreview
@Composable
private fun PlayGlyphPreview() = PreviewSurface { PlayGlyphComponent(color = MaterialTheme.colorScheme.primary) }
