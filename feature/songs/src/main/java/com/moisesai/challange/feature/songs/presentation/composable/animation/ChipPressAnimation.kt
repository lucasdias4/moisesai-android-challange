package com.moisesai.challange.feature.songs.presentation.composable.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.moisesai.challange.core.design.animation.SpringOvershootEasing
import com.moisesai.challange.core.design.graphics.BoxShadow
import com.moisesai.challange.core.design.graphics.cssBoxShadows

// A pressed chip lights up in lime in 0.4 seconds, with the springy overshoot that makes it feel pushed.
private const val CHIP_PRESS_MILLIS = 400
private const val PRESSED_SCALE = 1.07f

// Around a pressed chip there is a lime glow.
private val PressedGlowBlur = 26.dp
private const val PRESSED_GLOW_ALPHA = 0.5f

/** 0 while the chip rests and 1 while it is pressed; it overshoots a little on the way, and on the way back. */
@Composable
internal fun rememberChipPressProgress(interactionSource: InteractionSource): State<Float> {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(CHIP_PRESS_MILLIS, easing = SpringOvershootEasing),
        label = "chipPressProgress",
    )
}

/** The chip grows and glows [pressProgress] of the way to its pressed look. Read while drawing. */
internal fun Modifier.chipPressAnimation(pressProgress: () -> Float, shape: Shape, glow: Color): Modifier =
    this
        .graphicsLayer {
            val scale = lerp(1f, PRESSED_SCALE, pressProgress())
            scaleX = scale
            scaleY = scale
        }
        .cssBoxShadows(shape) {
            val level = pressProgress().coerceIn(0f, 1f)
            listOf(BoxShadow(glow.copy(alpha = PRESSED_GLOW_ALPHA * level), blur = PressedGlowBlur * level))
        }
