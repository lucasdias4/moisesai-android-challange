package com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.moisesai.challange.core.design.components.SkeletonBlockComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackCoverCorner
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackCoverMaxSize

/** Stands where [CoverSection] will be while the track is prepared: a block with the size and the corners of the cover. */
@Composable
internal fun CoverSkeletonSection(spaceAbove: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(top = spaceAbove), contentAlignment = Alignment.TopCenter) {
        SkeletonBlockComponent(
            modifier = Modifier.sizeIn(maxWidth = TrackCoverMaxSize, maxHeight = TrackCoverMaxSize).aspectRatio(1f),
            shape = RoundedCornerShape(TrackCoverCorner),
        )
    }
}

@FullPreview
@Composable
private fun CoverSkeletonSectionPreview() = PreviewSurface { CoverSkeletonSection(spaceAbove = PortraitSpaceAboveCover) }
