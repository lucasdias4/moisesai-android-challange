package com.moisesai.challange.feature.songs.presentation.composable.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import com.moisesai.challange.core.design.components.GlassState
import com.moisesai.challange.core.design.components.rememberGlassState
import com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse.HeaderCollapseDistance
import com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse.headerCollapseProgress

/**
 * What the parts of the Songs screen need to follow its scrolling: the position of the list, the glass that blurs
 * what scrolls under the floating parts, and how far the big header has collapsed (0 = in view, 1 = gone).
 */
@Stable
internal class ScrollContext(
    val listState: LazyListState,
    val glassState: GlassState,
    val collapseProgress: () -> Float,
)

@Composable
internal fun rememberScrollContext(): ScrollContext {
    val listState = rememberLazyListState()
    val glassState = rememberGlassState()
    val collapseDistancePx = with(LocalDensity.current) { HeaderCollapseDistance.toPx() }
    return remember(listState, glassState, collapseDistancePx) {
        ScrollContext(listState, glassState) { listState.headerCollapseProgress(collapseDistancePx) }
    }
}
