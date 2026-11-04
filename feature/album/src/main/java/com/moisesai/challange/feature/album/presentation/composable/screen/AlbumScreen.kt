package com.moisesai.challange.feature.album.presentation.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.StateAnimation
import com.moisesai.challange.core.design.components.ErrorMessageComponent
import com.moisesai.challange.core.design.components.TopBarComponent
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.album.presentation.composable.callback.AlbumCallbacks
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.composable.screen.album_header_section.AlbumHeaderSection
import com.moisesai.challange.feature.album.presentation.composable.screen.album_header_section.AlbumHeaderSkeletonSection
import com.moisesai.challange.feature.album.presentation.composable.screen.album_tracks_section.AlbumTracksSection
import com.moisesai.challange.feature.album.presentation.composable.screen.album_tracks_section.AlbumTracksSkeletonSection
import com.moisesai.challange.feature.album.presentation.model.UiAlbumUiState

// The design leaves this much between the top bar and the cover, and between the names and the first track.
private val SpaceBelowTopBar = 8.dp
private val SpaceBelowHeader = 40.dp
private const val NO_TITLE = ""

/** An album: a top bar with its name and, below it, the album, a skeleton of it while it comes or the reason it did not. */
@Composable
internal fun AlbumScreen(uiState: UiAlbumUiState, callbacks: AlbumCallbacks, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.design.colors.background00)) {
        TopBarComponent(title = uiState.topBarTitle(), onBackClick = callbacks.onBackClick)
        // A change of kind (the skeleton that becomes the album, or an error) cross-fades.
        StateAnimation(state = uiState, modifier = Modifier.weight(1f).fillMaxWidth()) { state ->
            Column(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    UiAlbumUiState.Loading -> AlbumLayout(
                        header = { AlbumHeaderSkeletonSection() },
                        tracks = { tracksModifier -> AlbumTracksSkeletonSection(modifier = tracksModifier) },
                    )

                    is UiAlbumUiState.Error -> ErrorMessageComponent(type = state.type, onRetry = callbacks.onRetry)

                    is UiAlbumUiState.Success -> AlbumLayout(
                        header = { AlbumHeaderSection(album = state.album) },
                        tracks = { tracksModifier ->
                            AlbumTracksSection(
                                tracks = state.album.tracks,
                                onTrackClick = callbacks.onTrackClick,
                                modifier = tracksModifier,
                            )
                        },
                    )
                }
            }
        }
    }
}

// The album has no name to show until it arrives.
private fun UiAlbumUiState.topBarTitle(): String = if (this is UiAlbumUiState.Success) album.name else NO_TITLE

// The album and its skeleton share this layout, so the skeleton stands exactly where the album will be.
@Composable
private fun ColumnScope.AlbumLayout(header: @Composable () -> Unit, tracks: @Composable (Modifier) -> Unit) {
    Spacer(modifier = Modifier.height(SpaceBelowTopBar))
    header()
    Spacer(modifier = Modifier.height(SpaceBelowHeader))
    tracks(Modifier.weight(1f))
}

@FullPreview
@Composable
private fun AlbumScreenContentPreview() = MoisesaiChallangeTheme {
    AlbumScreen(uiState = UiAlbumUiState.Success(SampleAlbum), callbacks = AlbumCallbacks.None)
}

@FullPreview
@Composable
private fun AlbumScreenWithoutTracksPreview() = MoisesaiChallangeTheme {
    AlbumScreen(uiState = UiAlbumUiState.Success(SampleAlbum.copy(tracks = emptyList())), callbacks = AlbumCallbacks.None)
}

@FullPreview
@Composable
private fun AlbumScreenLoadingPreview() = MoisesaiChallangeTheme {
    AlbumScreen(uiState = UiAlbumUiState.Loading, callbacks = AlbumCallbacks.None)
}

@FullPreview
@Composable
private fun AlbumScreenErrorPreview() = MoisesaiChallangeTheme {
    AlbumScreen(uiState = UiAlbumUiState.Error(UiErrorType.Network), callbacks = AlbumCallbacks.None)
}
