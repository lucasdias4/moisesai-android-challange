package com.moisesai.challange.feature.songs.presentation.composable.animation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.CssEaseInOutEasing
import com.moisesai.challange.core.design.graphics.BoxShadow
import com.moisesai.challange.core.design.graphics.cssBoxShadows

// The light breathes once every 2.2 seconds: 1.1 seconds to its fullest and 1.1 seconds back. It is a hint that
// this is the place to start, and it stops the moment the field is used.
private const val HINT_HALF_CYCLE_MILLIS = 1_100

// At its fullest the hint is a half-strong ring on the edge and a soft glow all around.
private val HintRingWidth = 1.5.dp
private const val HINT_RING_ALPHA = 0.55f
private val HintGlowBlur = 36.dp
private const val HINT_GLOW_ALPHA = 0.25f

/** While [isActive] the field pulses a faint lime light, inviting a first search. */
@Composable
internal fun Modifier.searchFieldHintAnimation(isActive: Boolean, shape: Shape): Modifier {
    if (!isActive) return this
    // 0 = no light, 1 = the fullest. Read while drawing, so the pulse never recomposes the screen.
    val breath = rememberInfiniteTransition(label = "searchFieldHint").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(HINT_HALF_CYCLE_MILLIS, easing = CssEaseInOutEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "searchFieldHintBreath",
    )
    val lime = MaterialTheme.colorScheme.primary
    return this.cssBoxShadows(shape) {
        val level = breath.value
        listOf(
            BoxShadow(lime.copy(alpha = HINT_RING_ALPHA * level), blur = 0.dp, spread = HintRingWidth * level),
            BoxShadow(lime.copy(alpha = HINT_GLOW_ALPHA * level), blur = HintGlowBlur * level),
        )
    }
}
