package com.moisesai.challange.splash.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp

// At the end of the exit the content is 8% bigger, which feels like it moves toward the viewer.
private const val EXIT_END_SCALE = 1.08f

/** Fades the content out while it grows slightly, once [phase] is [SplashPhase.Exiting]. */
@Composable
internal fun Modifier.splashExitAnimation(phase: SplashPhase): Modifier {
    // 0 = normal, 1 = fully exited. Lasts exactly as long as the Exiting phase.
    val exitProgress by animateFloatAsState(
        targetValue = if (phase == SplashPhase.Exiting) 1f else 0f,
        animationSpec = tween(SplashPhase.Exiting.durationMillis.toInt(), easing = FastOutSlowInEasing),
        label = "exitProgress",
    )
    // Returns a Modifier, so any content can be wrapped by one call. graphicsLayer avoids recomposition.
    return graphicsLayer {
        alpha = 1f - exitProgress
        scaleX = lerp(1f, EXIT_END_SCALE, exitProgress)
        scaleY = scaleX
    }
}
