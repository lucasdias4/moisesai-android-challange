package com.moisesai.challange.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val CoverCorner = 8.dp
private val FieldCorner = 12.dp

/** The corners of the design: small for covers and rows, medium for fields. Components never set a radius. */
internal val DesignShapes = Shapes(
    small = RoundedCornerShape(CoverCorner),
    medium = RoundedCornerShape(FieldCorner),
)
