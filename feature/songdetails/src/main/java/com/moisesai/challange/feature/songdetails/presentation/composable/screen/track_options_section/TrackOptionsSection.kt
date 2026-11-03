package com.moisesai.challange.feature.songdetails.presentation.composable.screen.track_options_section

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
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

/**
 * The sheet with the choices for the track that is playing, over the screen. It is drawn only while [isVisible].
 * Tapping anywhere outside it, or going back, closes it.
 */
@Composable
internal fun TrackOptionsSection(
    isVisible: Boolean,
    playingTrack: UiPlayingTrack,
    glassState: UiGlassState,
    callbacks: SongDetailsCallbacks,
    modifier: Modifier = Modifier,
) {
    if (!isVisible) return
    BackHandler(onBack = callbacks.onTrackOptionsDismiss)
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = callbacks.onTrackOptionsDismiss),
        )
        ActionSheetComponent(
            title = playingTrack.trackName,
            subtitle = playingTrack.artistName,
            glassState = glassState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ActionSheetItemComponent(
                iconRes = DesignR.drawable.ic_setlist,
                text = stringResource(R.string.common_view_album),
                onClick = { callbacks.onViewAlbumClick(playingTrack.albumId) },
            )
        }
    }
}

@FullPreview
@Composable
private fun TrackOptionsSectionPreview() = PreviewSurface {
    TrackOptionsSection(
        isVisible = true,
        playingTrack = SamplePlayingTrack,
        glassState = rememberUiGlassState(),
        callbacks = SongDetailsCallbacks.None,
    )
}
