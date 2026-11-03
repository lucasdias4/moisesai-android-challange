package com.moisesai.challange.feature.songdetails.presentation.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.components.ErrorMessageComponent
import com.moisesai.challange.core.design.components.IconButtonComponent
import com.moisesai.challange.core.design.components.LoadingIndicatorComponent
import com.moisesai.challange.core.design.components.TopBarComponent
import com.moisesai.challange.core.design.extension.glassSource
import com.moisesai.challange.core.design.model.rememberUiGlassState
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section.CoverSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.playback_section.PlaybackSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.track_options_section.TrackOptionsSection
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiSongDetailsUiState

// The design leaves this much between the top bar and the cover.
private val SpaceBelowTopBar = 8.dp

/**
 * The player: a top bar, then the track that is playing with its cover and its controls, a loader while the track is
 * prepared, or the reason it could not play. The choices for the track open over it.
 */
@Composable
internal fun SongDetailsScreen(uiState: UiSongDetailsUiState, callbacks: SongDetailsCallbacks, modifier: Modifier = Modifier) {
    val glassState = rememberUiGlassState()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.design.colors.background00)) {
        Column(modifier = Modifier.fillMaxSize().glassSource(glassState)) {
            SongDetailsTopBar(uiState = uiState, callbacks = callbacks)
            when (uiState) {
                UiSongDetailsUiState.Loading -> LoadingIndicatorComponent()
                is UiSongDetailsUiState.Error -> ErrorMessageComponent(type = uiState.type, onRetry = callbacks.onRetry)
                is UiSongDetailsUiState.Success -> SongDetailsContent(playingTrack = uiState.playingTrack, callbacks = callbacks)
            }
        }
        if (uiState is UiSongDetailsUiState.Success) {
            TrackOptionsSection(
                isVisible = uiState.isTrackOptionsVisible,
                playingTrack = uiState.playingTrack,
                glassState = glassState,
                callbacks = callbacks,
            )
        }
    }
}

// The choices are about the track, so the button that opens them waits for the track.
@Composable
private fun SongDetailsTopBar(uiState: UiSongDetailsUiState, callbacks: SongDetailsCallbacks) {
    TopBarComponent(
        title = stringResource(R.string.songdetails_title),
        onBackClick = callbacks.onBackClick,
        endAction = if (uiState is UiSongDetailsUiState.Success) {
            { TrackOptionsButton(onClick = callbacks.onTrackOptionsClick) }
        } else {
            null
        },
    )
}

@Composable
private fun TrackOptionsButton(onClick: () -> Unit) {
    IconButtonComponent(
        iconRes = DesignR.drawable.ic_more,
        contentDescription = stringResource(R.string.common_more_options),
        onClick = onClick,
    )
}

@Composable
private fun ColumnScope.SongDetailsContent(playingTrack: UiPlayingTrack, callbacks: SongDetailsCallbacks) {
    Spacer(modifier = Modifier.height(SpaceBelowTopBar))
    CoverSection(artUrl = playingTrack.artUrl, modifier = Modifier.weight(1f))
    PlaybackSection(playingTrack = playingTrack, callbacks = callbacks)
}

private val SampleContent = UiSongDetailsUiState.Success(playingTrack = SamplePlayingTrack, isTrackOptionsVisible = false)

@FullPreview
@Composable
private fun SongDetailsScreenContentPreview() = MoisesaiChallangeTheme { SongDetailsScreen(uiState = SampleContent, callbacks = SongDetailsCallbacks.None) }

@FullPreview
@Composable
private fun SongDetailsScreenTrackOptionsPreview() = MoisesaiChallangeTheme {
    SongDetailsScreen(uiState = SampleContent.copy(isTrackOptionsVisible = true), callbacks = SongDetailsCallbacks.None)
}

@FullPreview
@Composable
private fun SongDetailsScreenLoadingPreview() = MoisesaiChallangeTheme { SongDetailsScreen(uiState = UiSongDetailsUiState.Loading, callbacks = SongDetailsCallbacks.None) }

@FullPreview
@Composable
private fun SongDetailsScreenErrorPreview() = MoisesaiChallangeTheme {
    SongDetailsScreen(uiState = UiSongDetailsUiState.Error(UiErrorType.Unknown), callbacks = SongDetailsCallbacks.None)
}
