package com.moisesai.challange.feature.songdetails.presentation.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.moisesai.challange.feature.songdetails.presentation.composable.component.MessageSnackbarHostComponent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.animation.StateAnimation
import com.moisesai.challange.core.design.components.ErrorMessageComponent
import com.moisesai.challange.core.design.components.IconButtonComponent
import com.moisesai.challange.core.design.components.TopBarComponent
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section.CoverSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section.CoverSkeletonSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section.LandscapeSpaceAboveCover
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section.PortraitSpaceAboveCover
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.playback_section.PlaybackSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.playback_section.PlaybackSkeletonSection
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.track_options_section.TrackOptionsSection
import com.moisesai.challange.feature.songdetails.presentation.model.UiSongDetailsUiState

// The design leaves this much between the top bar and the cover.
private val SpaceBelowTopBar = 8.dp

// The same room under the cover as the controls leave under themselves, for the system bar.
private val LandscapeSpaceBelowCover = 53.dp

/**
 * The player: a top bar, then the track that is playing with its cover and its controls, a skeleton of them while the track is
 * prepared, or the reason it could not play. The choices for the track open over it.
 */
@Composable
internal fun SongDetailsScreen(uiState: UiSongDetailsUiState, callbacks: SongDetailsCallbacks, modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.design.colors.background00)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SongDetailsTopBar(uiState = uiState, callbacks = callbacks)
            // A change of kind (the skeleton that becomes the track) cross-fades; the track's own updates do not.
            StateAnimation(state = uiState, modifier = Modifier.weight(1f).fillMaxWidth()) { state ->
                when (state) {
                    UiSongDetailsUiState.Loading -> SongDetailsLayout(
                        snackbarHostState = snackbarHostState,
                        cover = { spaceAbove, coverModifier -> CoverSkeletonSection(spaceAbove = spaceAbove, modifier = coverModifier) },
                        playback = { playbackModifier -> PlaybackSkeletonSection(modifier = playbackModifier) },
                    )
                    is UiSongDetailsUiState.Error -> ErrorMessageComponent(type = state.type, onRetry = callbacks.onRetry)
                    is UiSongDetailsUiState.Success -> SongDetailsLayout(
                        snackbarHostState = snackbarHostState,
                        cover = { spaceAbove, coverModifier ->
                            CoverSection(playingTrack = state.playingTrack, spaceAbove = spaceAbove, modifier = coverModifier)
                        },
                        playback = { playbackModifier ->
                            PlaybackSection(playingTrack = state.playingTrack, callbacks = callbacks, modifier = playbackModifier)
                        },
                    )
                }
            }
        }
        if (uiState is UiSongDetailsUiState.Success) {
            TrackOptionsSection(
                isVisible = uiState.isTrackOptionsVisible,
                playingTrack = uiState.playingTrack,
                callbacks = callbacks,
            )
            CannotPlayMessageEffect(
                isVisible = uiState.isCannotPlayMessageVisible,
                snackbarHostState = snackbarHostState,
                onShown = callbacks.onCannotPlayMessageShown,
            )
        }
    }
}

// Shows the message once and then tells the ViewModel. If the screen is recreated before the message ends (a rotation),
// the ViewModel was not told, so the message starts again in the new screen.
@Composable
private fun CannotPlayMessageEffect(isVisible: Boolean, snackbarHostState: SnackbarHostState, onShown: () -> Unit) {
    if (!isVisible) return
    val message = stringResource(R.string.songdetails_cannot_play)
    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(message)
        onShown()
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

// The cover and the playback section go in these two slots, so the real ones and their skeletons share one layout and the
// skeleton stands exactly where the track will be.
@Composable
private fun SongDetailsLayout(
    snackbarHostState: SnackbarHostState,
    cover: @Composable (spaceAbove: Dp, modifier: Modifier) -> Unit,
    playback: @Composable (modifier: Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        if (isLandscape) {
            LandscapeLayout(snackbarHostState = snackbarHostState, cover = cover, playback = playback)
        } else {
            PortraitLayout(snackbarHostState = snackbarHostState, cover = cover, playback = playback)
        }
    }
}

// The cover takes whatever height the controls leave, and the messages sit at the bottom of that space, right above the
// controls that the user just tapped.
@Composable
private fun PortraitLayout(
    snackbarHostState: SnackbarHostState,
    cover: @Composable (spaceAbove: Dp, modifier: Modifier) -> Unit,
    playback: @Composable (modifier: Modifier) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(SpaceBelowTopBar))
        Box(modifier = Modifier.weight(1f)) {
            cover(PortraitSpaceAboveCover, Modifier.fillMaxSize())
            MessageSnackbarHostComponent(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
        playback(Modifier)
    }
}

// There is no height to stack them, so the cover goes beside the controls. The controls scroll when the screen is too
// short for them (a large font), instead of being cut off.
@Composable
private fun LandscapeLayout(
    snackbarHostState: SnackbarHostState,
    cover: @Composable (spaceAbove: Dp, modifier: Modifier) -> Unit,
    playback: @Composable (modifier: Modifier) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(bottom = LandscapeSpaceBelowCover)) {
            cover(LandscapeSpaceAboveCover, Modifier.fillMaxSize())
            MessageSnackbarHostComponent(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
        playback(Modifier.weight(1f).verticalScroll(rememberScrollState()))
    }
}

private val SampleContent = UiSongDetailsUiState.Success(playingTrack = SamplePlayingTrack, isTrackOptionsVisible = false, isCannotPlayMessageVisible = false)

@FullPreview
@Composable
private fun SongDetailsScreenContentPreview() = MoisesaiChallangeTheme { SongDetailsScreen(uiState = SampleContent, callbacks = SongDetailsCallbacks.None) }

@Preview(name = "Phone - Landscape", widthDp = 915, heightDp = 411)
@Composable
private fun SongDetailsScreenLandscapePreview() = MoisesaiChallangeTheme { SongDetailsScreen(uiState = SampleContent, callbacks = SongDetailsCallbacks.None) }

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
