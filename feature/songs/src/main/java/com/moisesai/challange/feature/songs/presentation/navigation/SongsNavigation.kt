package com.moisesai.challange.feature.songs.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.core.design.animation.LocalAnimatedVisibilityScope
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.composable.screen.SongsScreen
import com.moisesai.challange.feature.songs.presentation.view_model.SongsViewModel
import kotlinx.serialization.Serializable

/** The route of the Songs screen, the start of the app. */
@Serializable
data object SongsRoute

/** Adds the Songs screen to the navigation graph. The app decides where a chosen track or its album leads. */
fun NavGraphBuilder.songsScreen(onTrackClick: (trackId: Long) -> Unit, onViewAlbumClick: (albumId: Long) -> Unit) {
    composable<SongsRoute> {
        val viewModel: SongsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val callbacks = remember(viewModel, onTrackClick, onViewAlbumClick) {
            SongsCallbacks(
                onQueryChange = viewModel::onQueryChange,
                onSearchSubmit = viewModel::onSearchSubmit,
                onSearchIconClick = viewModel::onSearchIconClick,
                onTrackClick = onTrackClick,
                onTrackOptionsClick = viewModel::onTrackOptionsClick,
                onTrackOptionsDismiss = viewModel::onTrackOptionsDismiss,
                onViewAlbumClick = { albumId ->
                    viewModel.onTrackOptionsDismiss()
                    onViewAlbumClick(albumId)
                },
                onLoadMore = viewModel::loadMore,
                onRefresh = viewModel::refresh,
                onRetry = viewModel::retry,
            )
        }
        // The covers of this screen fly to and from the next one while this screen is on its way in or out.
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            SongsScreen(uiState = uiState, callbacks = callbacks)
        }
    }
}
