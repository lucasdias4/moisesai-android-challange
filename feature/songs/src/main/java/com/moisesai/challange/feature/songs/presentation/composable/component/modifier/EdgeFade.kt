package com.moisesai.challange.feature.songs.presentation.composable.component.modifier

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

// The content softly fades out over this width at an edge where there is more to scroll to, so it does not look cut.
private val FadeWidth = 14.dp

/** Fades the left and right edges of a row, but only on a side where it can still scroll. Read while drawing. */
internal fun Modifier.horizontalEdgeFade(listState: LazyListState): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val fade = FadeWidth.toPx().coerceAtMost(size.width / 2f)
            val start = if (listState.canScrollBackward) 0f else 1f
            val end = if (listState.canScrollForward) 0f else 1f
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Black.copy(alpha = start),
                    fade / size.width to Color.Black,
                    1f - fade / size.width to Color.Black,
                    1f to Color.Black.copy(alpha = end),
                ),
                // Keeps the content only where the mask is opaque.
                blendMode = BlendMode.DstIn,
            )
        }
