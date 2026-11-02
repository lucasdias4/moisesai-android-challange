package com.moisesai.challange.core.design.ambient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import com.moisesai.challange.core.design.ambient.animation.rememberAmbientCrossfade
import com.moisesai.challange.core.design.graphics.ellipticalRadialGradient
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.AuroraMagenta
import com.moisesai.challange.core.design.theme.AuroraNight
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme

// Each layer is three soft glows: a big one hanging from the top, one on the right edge and a magenta one in the
// bottom left corner. Centre and radii are shares of the screen; alpha is the strength at the centre.
private const val TOP_GLOW_RADIUS_X = 1.10f
private const val TOP_GLOW_RADIUS_Y = 0.62f
private const val TOP_GLOW_ALPHA = 0.78f
private const val TOP_GLOW_FADE_END = 0.72f

private const val SIDE_GLOW_CENTER_Y = 0.58f
private const val SIDE_GLOW_RADIUS_X = 0.80f
private const val SIDE_GLOW_RADIUS_Y = 0.45f
private const val SIDE_GLOW_ALPHA = 0.32f
private const val SIDE_GLOW_FADE_END = 0.70f

private const val CORNER_GLOW_RADIUS_X = 0.70f
private const val CORNER_GLOW_RADIUS_Y = 0.40f
private const val CORNER_GLOW_ALPHA = 0.22f
private const val CORNER_GLOW_FADE_END = 0.70f

// The light fades away towards the bottom of the screen, so the lowest content sits on plain background.
private const val VIGNETTE_START = 0.55f
private const val VIGNETTE_END = 0.96f

/**
 * The whole-screen background of the app: the dark base, lit from above by [tint] (usually the colour of the
 * artwork in view). When [tint] changes the old and new light cross-fade slowly. Put it behind everything.
 */
@Composable
fun AmbientBackground(tint: Color, modifier: Modifier = Modifier) {
    val crossfade = rememberAmbientCrossfade(tint)
    val vignette = remember {
        Brush.verticalGradient(
            0f to AuroraNight.copy(alpha = 0f),
            VIGNETTE_START to AuroraNight.copy(alpha = 0f),
            VIGNETTE_END to AuroraNight,
            1f to AuroraNight,
        )
    }
    Box(modifier = modifier.fillMaxSize().background(AuroraNight)) {
        AmbientLayer(color = crossfade.firstColor, alpha = { crossfade.firstAlpha.value })
        AmbientLayer(color = crossfade.secondColor, alpha = { crossfade.secondAlpha.value })
        Box(modifier = Modifier.fillMaxSize().drawBehind { drawRect(vignette) })
    }
}

@Composable
private fun AmbientLayer(color: Color, alpha: () -> Float) {
    val glows = remember(color) { glowsOf(color) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            // The three glows fade as one picture, not one by one: that needs the layer drawn off screen first.
            .graphicsLayer {
                this.alpha = alpha()
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawBehind { glows.forEach { drawRect(it) } },
    )
}

// The glows from bottom to top, so the top glow is painted last. A glow ends in its own colour at zero strength
// and not in transparent black, which would darken the middle of the fade.
private fun glowsOf(color: Color): List<Brush> = listOf(
    ellipticalRadialGradient(
        center = { Offset(0f, it.height) },
        horizontalRadius = { it.width * CORNER_GLOW_RADIUS_X },
        verticalRadius = { it.height * CORNER_GLOW_RADIUS_Y },
        0f to AuroraMagenta.copy(alpha = CORNER_GLOW_ALPHA),
        CORNER_GLOW_FADE_END to AuroraMagenta.copy(alpha = 0f),
        1f to AuroraMagenta.copy(alpha = 0f),
    ),
    ellipticalRadialGradient(
        center = { Offset(it.width, it.height * SIDE_GLOW_CENTER_Y) },
        horizontalRadius = { it.width * SIDE_GLOW_RADIUS_X },
        verticalRadius = { it.height * SIDE_GLOW_RADIUS_Y },
        0f to color.copy(alpha = SIDE_GLOW_ALPHA),
        SIDE_GLOW_FADE_END to color.copy(alpha = 0f),
        1f to color.copy(alpha = 0f),
    ),
    ellipticalRadialGradient(
        center = { Offset(it.width / 2f, 0f) },
        horizontalRadius = { it.width * TOP_GLOW_RADIUS_X },
        verticalRadius = { it.height * TOP_GLOW_RADIUS_Y },
        0f to color.copy(alpha = TOP_GLOW_ALPHA),
        TOP_GLOW_FADE_END to color.copy(alpha = 0f),
        1f to color.copy(alpha = 0f),
    ),
)

@FullPreview
@Composable
private fun AmbientBackgroundPreview() {
    MoisesaiChallangeTheme {
        AmbientBackground(tint = ambientTintOf(Color(210, 115, 86)))
    }
}
