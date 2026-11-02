package com.moisesai.challange.feature.songs.presentation.composable.model

/** Everything the user can do on the Songs screen, so the screen takes one parameter and not six. */
internal data class SongsActions(
    val onQueryChange: (String) -> Unit,
    val onSearchSubmit: () -> Unit,
    val onTrackClick: (Long) -> Unit,
    val onLoadMore: () -> Unit,
    val onRefresh: () -> Unit,
    val onRetry: () -> Unit,
) {
    companion object {
        /** For previews and tests that do not care about the actions. */
        val None = SongsActions({}, {}, {}, {}, {}, {})
    }
}
