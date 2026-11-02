package com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/** The compact title at the top fades in as the big one leaves: invisible at progress 0, fully visible at 1. */
internal fun Modifier.compactHeaderRevealAnimation(collapseProgress: () -> Float): Modifier =
    graphicsLayer { alpha = collapseProgress() }
