package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.glassEdge
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.IdleEqualizerAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val StatePadding = 24.dp
private val SkeletonRowHeight = 70.dp
private val SkeletonRowSpacing = 4.dp
private const val SKELETON_ROW_COUNT = 5

/** Shown while the first page of a search loads: bouncing bars, then faint rows where the results will be. */
@Composable
internal fun LoadingStateComponent(modifier: Modifier = Modifier) {
    val loading = stringResource(R.string.songs_loading)
    Column(
        modifier = modifier
            .fillMaxWidth()
            // Announced once when it appears, so a screen reader user knows a search is running.
            .semantics { contentDescription = loading; liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SkeletonRowSpacing),
    ) {
        Box(modifier = Modifier.padding(vertical = StatePadding)) { IdleEqualizerAnimation() }
        repeat(SKELETON_ROW_COUNT) {
            // Decoration only: the faint blocks hold the place of the rows that are about to arrive.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SkeletonRowHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.aurora.colors.glassFill)
                    .glassEdge(MaterialTheme.shapes.medium),
            )
        }
    }
}

@FullPreview
@Composable
private fun LoadingStatePreview() = PreviewSurface { LoadingStateComponent() }
