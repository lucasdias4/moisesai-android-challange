package com.moisesai.challange.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val BadgeCorner = 10.dp
private val ThumbnailCorner = 18.dp
private val RowCorner = 24.dp
private val CardCorner = 32.dp

// Fully rounded ends: the search field, the chips and the buttons are pills.
private const val PILL_CORNER_PERCENT = 50

/**
 * The corner language of the app: extra small (badges), small (cover thumbnails), medium (rows),
 * large (cards) and extra large (pills). Components take their shape from here and never set a radius.
 */
internal val AuroraShapes = Shapes(
    extraSmall = RoundedCornerShape(BadgeCorner),
    small = RoundedCornerShape(ThumbnailCorner),
    medium = RoundedCornerShape(RowCorner),
    large = RoundedCornerShape(CardCorner),
    extraLarge = RoundedCornerShape(PILL_CORNER_PERCENT),
)
