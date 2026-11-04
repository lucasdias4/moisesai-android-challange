package com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.feature.songdetails.presentation.composable.animation.CoverScaleAnimation
import com.moisesai.challange.core.design.animation.SharedCoverAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackCoverComponent
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

// The design puts the cover this far below the top bar.
internal val PortraitSpaceAboveCover = 82.dp

// Landscape has no design and little height: the cover only keeps a small gap from the top bar.
internal val LandscapeSpaceAboveCover = 8.dp

/**
 * The cover of the track, in the middle of the width, [spaceAbove] below the top of the section. It is smaller while the
 * music is not playing, and it is the cover that flies in from the list or the album the track was chosen in.
 */
@Composable
internal fun CoverSection(playingTrack: UiPlayingTrack, spaceAbove: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(top = spaceAbove), contentAlignment = Alignment.TopCenter) {
        SharedCoverAnimation(trackId = playingTrack.id) {
            CoverScaleAnimation(isPlaying = playingTrack.isPlaying) {
                TrackCoverComponent(artUrl = playingTrack.artUrl, thumbnailArtUrl = playingTrack.thumbnailArtUrl)
            }
        }
    }
}

@FullPreview
@Composable
private fun CoverSectionPreview() = PreviewSurface { CoverSection(playingTrack = SamplePlayingTrack, spaceAbove = PortraitSpaceAboveCover) }
