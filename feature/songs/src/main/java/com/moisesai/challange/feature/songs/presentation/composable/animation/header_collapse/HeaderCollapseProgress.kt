package com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.dp

// The big title is gone after the list has scrolled this far; the progress goes from 0 to 1 over this distance.
internal val HeaderCollapseDistance = 110.dp

private const val NOT_COLLAPSED = 0f
private const val FULLY_COLLAPSED = 1f

/**
 * How far the big title has scrolled away: 0 = fully in view, 1 = gone. [collapseDistancePx] is the scroll
 * distance it takes to get from one to the other.
 */
internal fun LazyListState.headerCollapseProgress(collapseDistancePx: Float): Float =
    // Once the first item has left the screen the header is gone, whatever the offset says.
    if (firstVisibleItemIndex > 0) FULLY_COLLAPSED else collapseProgressAt(firstVisibleItemScrollOffset, collapseDistancePx)

/** The progress of a list scrolled [scrollOffsetPx] when the collapse takes [collapseDistancePx]; never outside 0 to 1. */
internal fun collapseProgressAt(scrollOffsetPx: Int, collapseDistancePx: Float): Float =
    (scrollOffsetPx / collapseDistancePx).coerceIn(NOT_COLLAPSED, FULLY_COLLAPSED)
