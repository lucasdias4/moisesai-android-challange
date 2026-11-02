package com.moisesai.challange.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.moisesai.challange.splash.SplashScreen
import kotlinx.serialization.Serializable

/** The route of the splash screen, the start of the app. */
@Serializable
internal data object SplashRoute

/** Adds the splash screen to the navigation graph. The app decides what comes after it. */
internal fun NavGraphBuilder.splashScreen(onFinished: () -> Unit) {
    composable<SplashRoute> {
        SplashScreen(onFinished = onFinished)
    }
}
