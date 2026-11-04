package com.moisesai.challange.feature.album.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.core.design.animation.LocalAnimatedVisibilityScope
import com.moisesai.challange.feature.album.presentation.composable.callback.AlbumCallbacks
import com.moisesai.challange.feature.album.presentation.composable.screen.AlbumScreen
import com.moisesai.challange.feature.album.presentation.view_model.AlbumViewModel
import kotlinx.serialization.Serializable

/** The route of the Album screen: which album to show. */
@Serializable
data class AlbumRoute(val albumId: Long)

/** Adds the Album screen to the navigation graph. The app decides where back and a chosen track lead. */
fun NavGraphBuilder.albumScreen(onBackClick: () -> Unit, onTrackClick: (trackId: Long) -> Unit) {
    composable<AlbumRoute> {
        val viewModel: AlbumViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val callbacks = remember(viewModel, onBackClick, onTrackClick) {
            AlbumCallbacks(onBackClick = onBackClick, onTrackClick = onTrackClick, onRetry = viewModel::onRetry)
        }
        // Shared animation: the cover flies between the album track row and the player the track was opened in.
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            AlbumScreen(uiState = uiState, callbacks = callbacks)
        }
    }
}
