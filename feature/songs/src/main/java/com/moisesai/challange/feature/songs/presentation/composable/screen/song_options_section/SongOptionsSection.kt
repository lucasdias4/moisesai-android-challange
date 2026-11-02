package com.moisesai.challange.feature.songs.presentation.composable.screen.song_options_section

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.components.ActionSheetComponent
import com.moisesai.challange.core.design.components.ActionSheetItemComponent
import com.moisesai.challange.core.design.model.UiGlassState
import com.moisesai.challange.core.design.model.rememberUiGlassState
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.model.UiTrackOptionsState

/**
 * The sheet with the choices for a song, over the screen. It is shown only while [trackOptions] is visible. Tapping
 * anywhere outside it, or going back, closes it.
 */
@Composable
internal fun SongOptionsSection(
    trackOptions: UiTrackOptionsState,
    glassState: UiGlassState,
    callbacks: SongsCallbacks,
    modifier: Modifier = Modifier,
) {
    if (trackOptions !is UiTrackOptionsState.Visible) return
    val track = trackOptions.track
    BackHandler(onBack = callbacks.onTrackOptionsDismiss)
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = callbacks.onTrackOptionsDismiss),
        )
        ActionSheetComponent(
            title = track.name,
            subtitle = track.artistName,
            glassState = glassState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ActionSheetItemComponent(
                iconRes = DesignR.drawable.ic_setlist,
                text = stringResource(R.string.songs_view_album),
                onClick = { callbacks.onViewAlbumClick(track.albumId) },
            )
        }
    }
}

@FullPreview
@Composable
private fun SongOptionsSectionPreview() = PreviewSurface {
    SongOptionsSection(
        trackOptions = UiTrackOptionsState.Visible(SampleTracks.first()),
        glassState = rememberUiGlassState(),
        callbacks = SongsCallbacks.None,
    )
}
