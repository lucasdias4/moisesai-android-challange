package com.moisesai.challange.core.design.text

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints

/**
 * Makes the text exactly as tall as [style]'s line height, with the text centred in that box.
 *
 * Compose never makes a single line shorter than its font (a 58 sp Inter line stays about 70 dp tall even when the
 * style asks for 55 dp), while the design lays text out in a box of exactly the line height. Centring the full
 * line in a box of the wanted height gives the same position as the design.
 */
fun Modifier.lineBox(style: TextStyle): Modifier = layout { measurable, constraints ->
    val lineHeight = style.lineHeight.toPx().toInt()
    val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    layout(placeable.width, lineHeight) {
        placeable.place(0, (lineHeight - placeable.height) / 2)
    }
}
