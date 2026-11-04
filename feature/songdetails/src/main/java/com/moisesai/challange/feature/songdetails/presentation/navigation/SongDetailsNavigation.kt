package com.moisesai.challange.feature.songdetails.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import com.moisesai.challange.core.design.animation.LocalAnimatedVisibilityScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.composable.screen.SongDetailsScreen
import com.moisesai.challange.feature.songdetails.presentation.view_model.SongDetailsViewModel
import kotlinx.serialization.Serializable

/** The route of the Player screen: which track to play. */
@Serializable
data class SongDetailsRoute(val trackId: Long)

/** Adds the Player screen to the navigation graph. The app decides where back and "View album" lead. */
fun NavGraphBuilder.songDetailsScreen(onBackClick: () -> Unit, onViewAlbumClick: (albumId: Long) -> Unit) {
    composable<SongDetailsRoute> {
        val viewModel: SongDetailsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val callbacks = remember(viewModel, onBackClick, onViewAlbumClick) {
            SongDetailsCallbacks(
                onBackClick = onBackClick,
                onTrackOptionsClick = viewModel::onTrackOptionsClick,
                onTrackOptionsDismiss = viewModel::onTrackOptionsDismiss,
                onViewAlbumClick = { albumId ->
                    viewModel.onTrackOptionsDismiss()
                    onViewAlbumClick(albumId)
                },
                onPlayPauseClick = viewModel::onPlayPauseClick,
                onTrackProgressBarChangedByUser = viewModel::onTrackProgressBarChangedByUser,
                onPreviousClick = viewModel::onPreviousClick,
                onNextClick = viewModel::goToNextTrack,
                onRepeatClick = viewModel::onRepeatClick,
                onRetry = viewModel::onRetry,
                onCannotPlayMessageShown = viewModel::onCannotPlayMessageShown,
            )
        }
        // Shared animation: the cover flies between the player and the song list or album row the track was opened from.
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            SongDetailsScreen(uiState = uiState, callbacks = callbacks)
        }
    }
}
