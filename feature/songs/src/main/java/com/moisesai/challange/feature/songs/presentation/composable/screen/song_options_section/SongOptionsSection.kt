package com.moisesai.challange.feature.songs.presentation.composable.screen.song_options_section

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.animation.SheetAnimation
import com.moisesai.challange.core.design.components.ActionSheetComponent
import com.moisesai.challange.core.design.components.ActionSheetItemComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import com.moisesai.challange.feature.songs.presentation.model.UiTrackOptionsState

/**
 * The sheet with the choices for a song, over the screen. It rises while [trackOptions] is visible and slides away when
 * it is not. Tapping outside it, pulling it down, or going back, closes it.
 */
@Composable
internal fun SongOptionsSection(trackOptions: UiTrackOptionsState, callbacks: SongsCallbacks, modifier: Modifier = Modifier) {
    // The sheet is still on screen while it slides away, after the options are gone, so it keeps the song it showed.
    val shownTrack = remember { mutableStateOf<UiTrack?>(null) }
    (trackOptions as? UiTrackOptionsState.Visible)?.let { visibleOptions -> shownTrack.value = visibleOptions.track }
    BackHandler(enabled = trackOptions is UiTrackOptionsState.Visible, onBack = callbacks.onTrackOptionsDismiss)
    // The sheet rises from the bottom over a soft scrim and slides down to close; pulling it down, or tapping outside it, closes it.
    SheetAnimation(
        isVisible = trackOptions is UiTrackOptionsState.Visible,
        onDismiss = callbacks.onTrackOptionsDismiss,
        modifier = modifier,
    ) {
        shownTrack.value?.let { track ->
            ActionSheetComponent(title = track.name, subtitle = track.artistName) {
                ActionSheetItemComponent(
                    iconRes = DesignR.drawable.ic_setlist,
                    text = stringResource(R.string.songs_view_album),
                    onClick = { callbacks.onViewAlbumClick(track.albumId) },
                )
            }
        }
    }
}

@FullPreview
@Composable
private fun SongOptionsSectionPreview() = PreviewSurface {
    SongOptionsSection(
        trackOptions = UiTrackOptionsState.Visible(SampleTracks.first()),
        callbacks = SongsCallbacks.None,
    )
}
