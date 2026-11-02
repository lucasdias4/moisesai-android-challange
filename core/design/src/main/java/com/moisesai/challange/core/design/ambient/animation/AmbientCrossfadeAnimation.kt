package com.moisesai.challange.core.design.ambient.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.moisesai.challange.core.design.animation.AmbientEasing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// A change of colour takes this long: slow enough to feel like light changing, not like a switch.
private const val CROSSFADE_MILLIS = 1_400

/**
 * Two colour layers that trade places. A new colour is put on the layer that is not showing and fades in while the
 * other fades out, so the colour never jumps and a change that arrives mid-fade starts from where the fade is.
 */
@Stable
class AmbientCrossfade {
    var firstColor by mutableStateOf(Color.Transparent)
        private set
    var secondColor by mutableStateOf(Color.Transparent)
        private set

    // 0 = hidden, 1 = fully shown. Read while drawing, so a frame of the fade never recomposes anything.
    val firstAlpha = Animatable(0f)
    val secondAlpha = Animatable(0f)

    private var firstIsShowing = false

    suspend fun showTint(tint: Color) = coroutineScope {
        val incomingIsFirst = firstIsShowing.not()
        firstIsShowing = incomingIsFirst
        if (incomingIsFirst) firstColor = tint else secondColor = tint
        val incomingAlpha = if (incomingIsFirst) firstAlpha else secondAlpha
        val outgoingAlpha = if (incomingIsFirst) secondAlpha else firstAlpha
        val spec = tween<Float>(durationMillis = CROSSFADE_MILLIS, easing = AmbientEasing)
        launch { incomingAlpha.animateTo(1f, spec) }
        launch { outgoingAlpha.animateTo(0f, spec) }
    }
}

/** Fades the background to [tint] whenever it changes. The first tint also fades in, from black. */
@Composable
fun rememberAmbientCrossfade(tint: Color): AmbientCrossfade {
    val crossfade = remember { AmbientCrossfade() }
    LaunchedEffect(tint) { crossfade.showTint(tint) }
    return crossfade
}
