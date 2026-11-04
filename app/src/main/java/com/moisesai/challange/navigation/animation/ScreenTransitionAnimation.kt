package com.moisesai.challange.navigation.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.moisesai.challange.splash.navigation.SplashRoute

// How far a screen slides while it fades, as a part of its width: 1 / 12. A hint of direction, not a push across the screen.
private const val SLIDE_PART = 12

// Going forward the new screen comes in from the right and the old one leaves to the left; going back is the opposite.
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.forwardEnterAnimation(): EnterTransition =
    if (initialState.destination.hasRoute<SplashRoute>()) {
        fadeIn(screenTween())
    } else {
        fadeIn(screenTween()) + slideInHorizontally(screenTween()) { width -> width / SLIDE_PART }
    }

// The splash only fades away: it has nothing to slide.
internal fun AnimatedContentTransitionScope<NavBackStackEntry>.forwardExitAnimation(): ExitTransition =
    if (initialState.destination.hasRoute<SplashRoute>()) {
        fadeOut(screenTween())
    } else {
        fadeOut(screenTween()) + slideOutHorizontally(screenTween()) { width -> -width / SLIDE_PART }
    }

internal fun backEnterAnimation(): EnterTransition =
    fadeIn(screenTween()) + slideInHorizontally(screenTween()) { width -> -width / SLIDE_PART }

internal fun backExitAnimation(): ExitTransition =
    fadeOut(screenTween()) + slideOutHorizontally(screenTween()) { width -> width / SLIDE_PART }
