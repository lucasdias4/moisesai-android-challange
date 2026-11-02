package com.moisesai.challange.navigation

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.moisesai.challange.feature.album.presentation.navigation.AlbumRoute
import com.moisesai.challange.feature.album.presentation.navigation.albumScreen
import com.moisesai.challange.feature.songdetails.presentation.navigation.PlayerRoute
import com.moisesai.challange.feature.songdetails.presentation.navigation.playerScreen
import com.moisesai.challange.feature.songs.presentation.navigation.SongsRoute
import com.moisesai.challange.feature.songs.presentation.navigation.songsScreen
import com.moisesai.challange.splash.navigation.SplashRoute
import com.moisesai.challange.splash.navigation.splashScreen

// The design fades from one screen to the next in 300 ms, easing out.
private const val SCREEN_FADE_MILLIS = 300

/**
 * The only place where the screens meet: it decides where each one leads.
 *
 * ```
 * Splash --after a moment--> Songs --song--------> Player --"View album"--> Album --song--> Player (replaces the first one)
 *                            Songs --"View album"-----------------------> Album
 * ```
 * Back returns to the screen before. The splash is not one of them: it leaves the back stack when it ends.
 */
@Composable
internal fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
        enterTransition = { fadeIn(tween(durationMillis = SCREEN_FADE_MILLIS, easing = EaseOut)) },
        exitTransition = { fadeOut(tween(durationMillis = SCREEN_FADE_MILLIS, easing = EaseOut)) },
    ) {
        splashScreen(onFinished = navController::openSongsReplacingTheSplash)
        songsScreen(
            onTrackClick = navController::openPlayer,
            onViewAlbumClick = navController::openAlbum,
        )
        playerScreen(
            onBackClick = navController::popBackStack,
            onViewAlbumClick = navController::openAlbum,
        )
        albumScreen(
            onBackClick = navController::popBackStack,
            onTrackClick = navController::openPlayerReplacingThePlayerBefore,
        )
    }
}

// Going back from Songs must leave the app, not show the splash again.
private fun NavController.openSongsReplacingTheSplash() {
    navigate(SongsRoute) {
        popUpTo<SplashRoute> { inclusive = true }
    }
}

private fun NavController.openPlayer(trackId: Long) {
    navigate(PlayerRoute(trackId))
}

private fun NavController.openAlbum(albumId: Long) {
    navigate(AlbumRoute(albumId))
}

// Only one song plays at a time, so a song chosen from an album takes the place of the player the user came from:
// going back from it leads to where that player was opened, not to a player of a song that stopped.
private fun NavController.openPlayerReplacingThePlayerBefore(trackId: Long) {
    navigate(PlayerRoute(trackId)) {
        popUpTo<PlayerRoute> { inclusive = true }
    }
}
