package com.moisesai.challange.feature.songs.presentation.composable.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private const val VIEWPORT = 24f
private const val LENS_CENTER = 11f
private const val LENS_RADIUS = 7f

// The magnifier of the design: a circle and a short handle, drawn as a line with square ends.
private fun searchGlyph(name: String, strokeWidth: Float): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, VIEWPORT, VIEWPORT).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = strokeWidth,
    ) {
        // A circle is two half circles.
        moveTo(LENS_CENTER + LENS_RADIUS, LENS_CENTER)
        arcTo(LENS_RADIUS, LENS_RADIUS, 0f, isMoreThanHalf = true, isPositiveArc = true, LENS_CENTER - LENS_RADIUS, LENS_CENTER)
        arcTo(LENS_RADIUS, LENS_RADIUS, 0f, isMoreThanHalf = true, isPositiveArc = true, LENS_CENTER + LENS_RADIUS, LENS_CENTER)
        moveTo(20f, 20f)
        lineTo(16f, 16f)
    }.build()

/** The magnifier in the search field, with a 2 unit line. */
internal val FieldSearchGlyph = searchGlyph("FieldSearchGlyph", strokeWidth = 2f)

/** The magnifier in a suggestion chip, with a slightly bolder 2.4 unit line, as small icons need. */
internal val ChipSearchGlyph = searchGlyph("ChipSearchGlyph", strokeWidth = 2.4f)
