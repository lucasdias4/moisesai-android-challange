package com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

// While it collapses the title shrinks by up to 42%, rises by up to 30 dp and fades by up to 85%.
private const val TITLE_SHRINK = 0.42f
private val TITLE_RISE = 30.dp
private const val TITLE_FADE = 0.85f

// The shrink is anchored at the top left of the title, like the design's transform origin.
private val TitleTopLeft = TransformOrigin(0f, 0f)

/** The big title shrinks, rises and fades as the list scrolls. [collapseProgress] is read while drawing. */
internal fun Modifier.titleCollapseAnimation(collapseProgress: () -> Float): Modifier =
    graphicsLayer {
        val progress = collapseProgress()
        val scale = 1f - TITLE_SHRINK * progress
        transformOrigin = TitleTopLeft
        scaleX = scale
        scaleY = scale
        // The rise is applied before the shrink in the design, so it is shrunk with everything else.
        translationY = -TITLE_RISE.toPx() * progress * scale
        alpha = 1f - TITLE_FADE * progress
    }
