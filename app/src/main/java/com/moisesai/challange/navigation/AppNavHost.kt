package com.moisesai.challange.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.moisesai.challange.core.design.animation.LocalSharedTransitionScope
import com.moisesai.challange.feature.album.presentation.navigation.AlbumRoute
import com.moisesai.challange.feature.album.presentation.navigation.albumScreen
import com.moisesai.challange.feature.songdetails.presentation.navigation.SongDetailsRoute
import com.moisesai.challange.feature.songdetails.presentation.navigation.songDetailsScreen
import com.moisesai.challange.feature.songs.presentation.navigation.SongsRoute
import com.moisesai.challange.feature.songs.presentation.navigation.songsScreen
import com.moisesai.challange.navigation.animation.backEnterAnimation
import com.moisesai.challange.navigation.animation.backExitAnimation
import com.moisesai.challange.navigation.animation.forwardEnterAnimation
import com.moisesai.challange.navigation.animation.forwardExitAnimation
import com.moisesai.challange.splash.navigation.SplashRoute
import com.moisesai.challange.splash.navigation.splashScreen

/**
 * The only place where the screens meet: it decides where each one leads.
 *
 * ```
 * Splash --after a moment--> Songs --track-----> Song details --"View album"--> Album --track--> Song details (replaces the first one)
 *                            Songs --"View album"-----------------------------> Album
 * ```
 * Back returns to the screen before. The splash is not one of them: it leaves the back stack when it ends.
 */
@Composable
internal fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // The covers of the tracks fly from the list or the album to the player and back: this is where they fly.
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = SplashRoute,
                modifier = modifier,
                enterTransition = { forwardEnterAnimation() },
                exitTransition = { forwardExitAnimation() },
                popEnterTransition = { backEnterAnimation() },
                popExitTransition = { backExitAnimation() },
            ) {
                splashScreen(onFinished = navController::openSongsReplacingTheSplash)
                songsScreen(
                    onTrackClick = navController::openSongDetails,
                    onViewAlbumClick = navController::openAlbum,
                )
                songDetailsScreen(
                    onBackClick = navController::popBackStack,
                    onViewAlbumClick = navController::openAlbum,
                )
                albumScreen(
                    onBackClick = navController::popBackStack,
                    onTrackClick = navController::openSongDetailsReplacingTheOneBefore,
                )
            }
        }
    }
}

// Going back from Songs must leave the app, not show the splash again.
private fun NavController.openSongsReplacingTheSplash() {
    navigate(SongsRoute) {
        popUpTo<SplashRoute> { inclusive = true }
    }
}

private fun NavController.openSongDetails(trackId: Long) {
    navigate(SongDetailsRoute(trackId))
}

private fun NavController.openAlbum(albumId: Long) {
    navigate(AlbumRoute(albumId))
}

// Only one track plays at a time, so a track chosen from an album takes the place of the song details it came from:
// going back from it leads to where those song details were opened, not to the details of a track that stopped.
private fun NavController.openSongDetailsReplacingTheOneBefore(trackId: Long) {
    navigate(SongDetailsRoute(trackId)) {
        popUpTo<SongDetailsRoute> { inclusive = true }
    }
}
