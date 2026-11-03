package com.moisesai.challange.feature.album.presentation.composable.callback

/** Everything the user can do on the album screen, so the screen takes one parameter and not three. */
internal data class AlbumCallbacks(
    val onBackClick: () -> Unit,
    val onTrackClick: (trackId: Long) -> Unit,
    val onRetry: () -> Unit,
) {
    companion object {
        /** For previews and tests that do not care about the callbacks. */
        val None = AlbumCallbacks({}, {}, {})
    }
}
