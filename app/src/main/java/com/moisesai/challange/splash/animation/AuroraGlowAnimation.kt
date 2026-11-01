package com.moisesai.challange.splash.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

// The glow fades in while growing from 60% of its size to 100%.
private const val ENTRY_MILLIS = 1_400
private const val ENTRY_START_SCALE = 0.6f
// Strength of the magenta centre and the lime ring. Low values keep it a soft haze, not a spotlight.
private const val SECONDARY_GLOW_ALPHA = 0.4f
private const val PRIMARY_GLOW_ALPHA = 0.16f
// Where the lime ring sits, as a share of the radius (0 = centre, 1 = edge).
private const val PRIMARY_GLOW_STOP = 0.55f

private val GlowSize = 340.dp

/** A soft magenta and lime halo that grows behind the wordmark once the bars start to collapse. */
@Composable
internal fun AuroraGlowAnimation(
    phase: SplashPhase,
    modifier: Modifier = Modifier,
) {
    // 0 = hidden, 1 = fully shown. Starts at Collapsing so the glow is ready when the wordmark arrives.
    val entryProgress by animateFloatAsState(
        targetValue = if (phase >= SplashPhase.Collapsing) 1f else 0f,
        animationSpec = tween(ENTRY_MILLIS, easing = FastOutSlowInEasing),
        label = "glowEntryProgress",
    )
    val secondary = MaterialTheme.colorScheme.secondary.copy(alpha = SECONDARY_GLOW_ALPHA)
    val primary = MaterialTheme.colorScheme.primary.copy(alpha = PRIMARY_GLOW_ALPHA)

    Box(
        modifier = modifier
            .size(GlowSize)
            // graphicsLayer animates alpha and scale without recomposing.
            .graphicsLayer {
                alpha = entryProgress
                scaleX = lerp(ENTRY_START_SCALE, 1f, entryProgress)
                scaleY = scaleX
            }
            // Radial gradient: magenta centre, then a lime ring, then fully transparent at the edge.
            .drawBehind {
                val halo = Brush.radialGradient(
                    colorStops = arrayOf(0f to secondary, PRIMARY_GLOW_STOP to primary, 1f to Color.Transparent),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.minDimension / 2,
                )
                drawCircle(brush = halo)
            },
    )
}
