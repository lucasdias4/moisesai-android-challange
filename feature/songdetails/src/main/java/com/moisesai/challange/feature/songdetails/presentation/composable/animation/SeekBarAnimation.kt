package com.moisesai.challange.feature.songdetails.presentation.composable.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

// The player says where it is every 250 ms. The bar takes that long to get there, at a steady pace, so it flows and does not tick.
private const val PLAYER_UPDATE_MILLIS = 250

// How big the handle is while the finger holds it: 1 = its normal size.
private const val HELD_HANDLE_SCALE = 1.4f

/**
 * Makes the progress of the bar (0 to 1) flow to each new value instead of ticking; while the finger holds the bar it follows at once.
 * Used in `SeekBarComponent`: `val flowingProgress = animateSeekProgress(progress = shownProgress, isHeld = isHeld)`.
 */
@Composable
internal fun animateSeekProgress(progress: Float, isHeld: Boolean): State<Float> = animateFloatAsState(
    targetValue = progress,
    animationSpec = if (isHeld) snap() else tween(PLAYER_UPDATE_MILLIS, easing = LinearEasing),
    label = "seekProgress",
)

/**
 * Makes the handle of the bar grow while the finger holds it, so the user sees it was caught.
 * Used in `SeekBarComponent`: `val handleScale = animateHandleScale(isHeld = isHeld)`.
 */
@Composable
internal fun animateHandleScale(isHeld: Boolean): State<Float> = animateFloatAsState(
    targetValue = if (isHeld) HELD_HANDLE_SCALE else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    label = "handleScale",
)
