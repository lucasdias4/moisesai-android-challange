package com.moisesai.challange.feature.songs.presentation.composable.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.CssEaseEasing
import com.moisesai.challange.core.design.animation.SpringOvershootEasing
import com.moisesai.challange.core.design.graphics.BoxShadow
import com.moisesai.challange.core.design.graphics.cssBoxShadows

// The field grows by 1.5% when focused: enough to feel alive, too little to move the layout around it.
private const val FOCUSED_SCALE = 1.015f
private const val FOCUS_MILLIS = 500

// Three layers of light: a bright ring right on the edge, a wider and much fainter ring around it, and a soft
// glow that falls below the field. Each is shown at full size and strength once the field is focused.
private val RingWidth = 1.5.dp
private val HaloWidth = 6.dp
private const val HALO_ALPHA = 0.14f
private val GlowOffset = 10.dp
private val GlowBlur = 50.dp
private const val GLOW_ALPHA = 0.28f

/** While [isFocused] the field grows a little and is lit by a lime ring, halo and glow that grow out of its edge. */
@Composable
internal fun Modifier.searchFieldFocusAnimation(isFocused: Boolean, shape: Shape): Modifier {
    // The size change is springy, the light is a smooth fade: the same split the design uses.
    val scale = animateFloatAsState(
        targetValue = if (isFocused) FOCUSED_SCALE else 1f,
        animationSpec = tween(FOCUS_MILLIS, easing = SpringOvershootEasing),
        label = "searchFieldScale",
    )
    // 0 = no light, 1 = full light. Read while drawing, so the glow never recomposes the screen.
    val light = animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(FOCUS_MILLIS, easing = CssEaseEasing),
        label = "searchFieldLight",
    )
    val lime = MaterialTheme.colorScheme.primary
    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .cssBoxShadows(shape) {
            val level = light.value.coerceIn(0f, 1f)
            listOf(
                BoxShadow(lime.copy(alpha = level), blur = 0.dp, spread = RingWidth * level),
                BoxShadow(lime.copy(alpha = HALO_ALPHA * level), blur = 0.dp, spread = HaloWidth * level),
                BoxShadow(
                    lime.copy(alpha = GLOW_ALPHA * level),
                    blur = GlowBlur * level,
                    offsetY = GlowOffset * level,
                ),
            )
        }
}
