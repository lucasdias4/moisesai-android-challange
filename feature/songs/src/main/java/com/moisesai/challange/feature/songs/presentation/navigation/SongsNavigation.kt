package com.moisesai.challange.feature.songs.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.SongsScreen
import com.moisesai.challange.feature.songs.presentation.view_model.SongsViewModel
import kotlinx.serialization.Serializable

/** The route of the Songs screen, the start of the app. */
@Serializable
data object SongsRoute

/** Adds the Songs screen to the navigation graph. The app decides what happens when a track is chosen. */
fun NavGraphBuilder.songsScreen(onTrackClick: (trackId: Long) -> Unit) {
    composable<SongsRoute> {
        val viewModel: SongsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val actions = remember(viewModel, onTrackClick) {
            SongsActions(
                onQueryChange = viewModel::onQueryChange,
                onSearchSubmit = viewModel::onSearchSubmit,
                onSuggestionClick = viewModel::onSuggestionClick,
                onTrackClick = onTrackClick,
                onLoadMore = viewModel::loadMore,
                onRefresh = viewModel::refresh,
                onRetry = viewModel::retry,
            )
        }
        SongsScreen(uiState = uiState, actions = actions)
    }
}
