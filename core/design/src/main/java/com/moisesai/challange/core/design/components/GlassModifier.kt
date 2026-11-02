package com.moisesai.challange.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.ambient.AmbientBackground
import com.moisesai.challange.core.design.graphics.angledLinearGradient
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.AuroraLime
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.design.theme.aurora
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val DefaultGlassBlur = 26.dp
private val GlassEdgeWidth = 1.dp

// The edge catches light from the top left: bright where it meets that corner, almost gone through the middle,
// and a touch of lime at the far corner. Each pair is (position along the gradient, white or lime at that point).
private const val GLASS_EDGE_ANGLE_DEGREES = 140f
private val GlassEdge = angledLinearGradient(
    GLASS_EDGE_ANGLE_DEGREES,
    0f to Color.White.copy(alpha = 0.5f),
    0.38f to Color.White.copy(alpha = 0.06f),
    0.6f to Color.White.copy(alpha = 0.02f),
    1f to AuroraLime.copy(alpha = 0.38f),
)

/** How a glass surface looks: its [shape], the [fill] laid over the blur, how strong the [blur] is, and an optional [mask]. */
@Immutable
class GlassStyle(
    val shape: Shape,
    val fill: Color,
    val blur: Dp = DefaultGlassBlur,
    /** What to paint instead of the blur where it cannot be done (Android before 12): a stronger fill. */
    val fallbackFill: Color = fill,
    /** Where the glass is visible: opaque parts show it, transparent parts do not. */
    val mask: Brush? = null,
)

/**
 * The place where glass finds what is behind it. It wraps the blur library, so the rest of the app never sees it
 * and the way the blur is made can change without touching a screen.
 */
@Stable
class GlassState internal constructor(internal val blur: HazeState)

/** Remembers the place where glass finds what is behind it. Give it to [glassSource] and to every [glass]. */
@Composable
fun rememberGlassState(): GlassState {
    val blur = rememberHazeState()
    return remember(blur) { GlassState(blur) }
}

/** Marks this content as what the glass surfaces drawn over it blur. */
fun Modifier.glassSource(state: GlassState): Modifier = hazeSource(state.blur)

/** A surface of frosted glass: it blurs what is behind it ([glassSource]) and lays a faint [GlassStyle.fill] over it. */
fun Modifier.glass(state: GlassState, style: GlassStyle): Modifier =
    this
        .clip(style.shape)
        .hazeEffect(state.blur) {
            blurRadius = style.blur
            tints = listOf(HazeTint(style.fill))
            fallbackTint = HazeTint(style.fallbackFill)
            // The design's glass is smooth: no grain of its own, the screen has one grain on top of everything.
            noiseFactor = 0f
            mask = style.mask
        }

/** The thin edge that catches the light, for the glass surfaces that have one. */
fun Modifier.glassEdge(shape: Shape): Modifier = border(GlassEdgeWidth, GlassEdge, shape)

@FullPreview
@Composable
private fun GlassPreview() {
    MoisesaiChallangeTheme {
        val state = rememberGlassState()
        // Behind the glass there is colour, which is what makes the surface read as glass.
        Box(modifier = Modifier.fillMaxSize().glassSource(state), contentAlignment = Alignment.Center) {
            AmbientBackground(tint = AuroraLime.copy(alpha = 0.8f))
            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 96.dp)
                    .glass(state, GlassStyle(MaterialTheme.shapes.large, MaterialTheme.aurora.colors.glassFill))
                    .glassEdge(MaterialTheme.shapes.large),
            )
        }
    }
}
