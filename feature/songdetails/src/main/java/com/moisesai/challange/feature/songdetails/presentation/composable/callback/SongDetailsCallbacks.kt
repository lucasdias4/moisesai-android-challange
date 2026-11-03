package com.moisesai.challange.feature.songdetails.presentation.composable.callback

/** Everything the user can do on the player screen, so the screen takes one parameter and not ten. */
internal data class SongDetailsCallbacks(
    val onBackClick: () -> Unit,
    val onTrackOptionsClick: () -> Unit,
    val onTrackOptionsDismiss: () -> Unit,
    val onViewAlbumClick: (albumId: Long) -> Unit,
    val onPlayPauseClick: () -> Unit,
    /** Jumps to a point of the track: 0 is its start and 1 is its end. */
    val onTrackProgressBarChangedByUser: (changedProgress: Float) -> Unit,
    val onPreviousClick: () -> Unit,
    val onNextClick: () -> Unit,
    val onRepeatClick: () -> Unit,
    val onRetry: () -> Unit,
) {
    companion object {
        /** For previews and tests that do not care about the callbacks. */
        val None = SongDetailsCallbacks({}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
