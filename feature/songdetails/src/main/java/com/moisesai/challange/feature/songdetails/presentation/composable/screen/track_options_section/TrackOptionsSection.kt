package com.moisesai.challange.feature.songdetails.presentation.composable.screen.track_options_section

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.animation.SheetAnimation
import com.moisesai.challange.core.design.components.ActionSheetComponent
import com.moisesai.challange.core.design.components.ActionSheetItemComponent
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

/**
 * The sheet with the choices for the track that is playing, over the screen. It rises while [isVisible] and slides away
 * when it is not. Tapping outside it, pulling it down, or going back, closes it.
 */
@Composable
internal fun TrackOptionsSection(
    isVisible: Boolean,
    playingTrack: UiPlayingTrack,
    callbacks: SongDetailsCallbacks,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = isVisible, onBack = callbacks.onTrackOptionsDismiss)
    SheetAnimation(isVisible = isVisible, onDismiss = callbacks.onTrackOptionsDismiss, modifier = modifier) {
        ActionSheetComponent(title = playingTrack.trackName, subtitle = playingTrack.artistName) {
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
        callbacks = SongDetailsCallbacks.None,
    )
}
