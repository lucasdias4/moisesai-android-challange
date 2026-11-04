package com.moisesai.challange.feature.songs.presentation.composable.callback

import com.moisesai.challange.feature.songs.presentation.model.UiTrack

/** Everything the user can do on the Songs screen, so the screen takes one parameter and not ten. */
internal data class SongsCallbacks(
    val onQueryChange: (String) -> Unit,
    val onSearchSubmit: () -> Unit,
    val onSearchIconClick: () -> Unit,
    val onTrackClick: (Long) -> Unit,
    val onTrackOptionsClick: (UiTrack) -> Unit,
    val onTrackOptionsDismiss: () -> Unit,
    val onViewAlbumClick: (albumId: Long) -> Unit,
    val onLoadMore: () -> Unit,
    val onRefresh: () -> Unit,
    val onRetry: () -> Unit,
) {
    companion object {
        /** For previews and tests that do not care about the callbacks. */
        val None = SongsCallbacks({}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
