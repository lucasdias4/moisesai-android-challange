package com.moisesai.challange.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.moisesai.challange.feature.songs.presentation.navigation.SongsRoute
import com.moisesai.challange.feature.songs.presentation.navigation.songsScreen

/** The only place where the screens of the features meet: it decides where each one leads. */
@Composable
internal fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SongsRoute, modifier = modifier) {
        // The player does not exist yet, so choosing a track leads nowhere until :feature:songdetails is built.
        songsScreen(onTrackClick = {})
    }
}
