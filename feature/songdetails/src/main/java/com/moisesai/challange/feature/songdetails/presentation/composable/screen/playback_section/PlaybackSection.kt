package com.moisesai.challange.feature.songdetails.presentation.composable.screen.playback_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songdetails.presentation.composable.component.PlaybackControlsComponent
import com.moisesai.challange.feature.songdetails.presentation.composable.component.PlaybackTimesComponent
import com.moisesai.challange.feature.songdetails.presentation.composable.component.SeekBarComponent
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackNamesComponent
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

private val SectionHorizontalPadding = 24.dp
private val SectionTopPadding = 12.dp

// 12 of padding, and 41 more that the design leaves under the controls: room for the system bar.
private val SectionBottomPadding = 53.dp
private val SpaceBetweenParts = 20.dp
private val SpaceBetweenSeekBarAndTimes = 4.dp

/** What is playing and how to control it: the names of the track, how far it has played, and the controls. */
@Composable
internal fun PlaybackSection(playingTrack: UiPlayingTrack, callbacks: SongDetailsCallbacks, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = SectionHorizontalPadding, top = SectionTopPadding, end = SectionHorizontalPadding, bottom = SectionBottomPadding),
        verticalArrangement = Arrangement.spacedBy(SpaceBetweenParts),
    ) {
        TrackNamesComponent(trackName = playingTrack.trackName, artistName = playingTrack.artistName)
        Column(verticalArrangement = Arrangement.spacedBy(SpaceBetweenSeekBarAndTimes)) {
            SeekBarComponent(progress = playingTrack.progress, onProgressChangedByUser = callbacks.onTrackProgressBarChangedByUser)
            PlaybackTimesComponent(
                playedTimeText = playingTrack.playedTimeText,
                remainingTimeText = playingTrack.remainingTimeText,
            )
        }
        PlaybackControlsComponent(playingTrack = playingTrack, callbacks = callbacks)
    }
}

@FullPreview
@Composable
private fun PlaybackSectionPreview() = PreviewSurface { PlaybackSection(playingTrack = SamplePlayingTrack, callbacks = SongDetailsCallbacks.None) }
