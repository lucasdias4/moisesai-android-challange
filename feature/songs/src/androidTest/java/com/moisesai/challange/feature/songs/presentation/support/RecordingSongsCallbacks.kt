package com.moisesai.challange.feature.songs.presentation.support

import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

/** Keeps what the user did on the screen, so a test can say what was reported. */
internal class RecordingSongsCallbacks {
    val typedQueries = mutableListOf<String>()
    var submitCount = 0
    var searchIconClickCount = 0
    val clickedTrackIds = mutableListOf<Long>()
    val optionsTracks = mutableListOf<UiTrack>()
    var optionsDismissCount = 0
    val albumIds = mutableListOf<Long>()
    var loadMoreCount = 0
    var refreshCount = 0
    var retryCount = 0

    fun asSongsCallbacks() = SongsCallbacks(
        onQueryChange = { typedQueries += it },
        onSearchSubmit = { submitCount++ },
        onSearchIconClick = { searchIconClickCount++ },
        onTrackClick = { clickedTrackIds += it },
        onTrackOptionsClick = { optionsTracks += it },
        onTrackOptionsDismiss = { optionsDismissCount++ },
        onViewAlbumClick = { albumIds += it },
        onLoadMore = { loadMoreCount++ },
        onRefresh = { refreshCount++ },
        onRetry = { retryCount++ },
    )
}
