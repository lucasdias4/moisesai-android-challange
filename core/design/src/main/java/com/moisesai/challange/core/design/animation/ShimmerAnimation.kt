package com.moisesai.challange.core.design.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.coroutines.launch

// One pass of the light across the screen. Slow enough to feel calm, fast enough to read as "loading".
private const val SWEEP_MILLIS = 1_500

// How wide the light is, as a part of the window's width: a soft band, not a thin line.
private const val LIGHT_WIDTH_PART = 0.4f

/**
 * Sweeps a band of light across the window again and again, so every block that has it shines as one.
 * Used in `SkeletonBlockComponent`: `Box(modifier.clip(shape).background(colors.alphaInvert10).shimmerAnimation(colors.whiteAlpha20))`.
 */
internal fun Modifier.shimmerAnimation(lightColor: Color): Modifier = this then ShimmerElement(lightColor)

private data class ShimmerElement(val lightColor: Color) : ModifierNodeElement<ShimmerNode>() {
    override fun create() = ShimmerNode(lightColor)

    override fun update(node: ShimmerNode) {
        node.lightColor = lightColor
    }
}

private class ShimmerNode(var lightColor: Color) :
    Modifier.Node(),
    DrawModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode {

    // 0 = the light is just left of the window, 1 = it is just right of it. It is read only while drawing, so a frame
    // redraws the block and never recomposes it.
    private val sweep = Animatable(0f)

    // Where the block starts in the window: the light is drawn in the window's space, not in the block's.
    private var blockLeftInWindow = 0f

    // The system's animation scale applies here: with animations turned off the light does not move.
    override fun onAttach() {
        coroutineScope.launch {
            sweep.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(SWEEP_MILLIS, easing = LinearEasing), RepeatMode.Restart),
            )
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        blockLeftInWindow = coordinates.positionInWindow().x
    }

    // The band is built once and only moved while drawing: a new shader on every frame, for every block, would be waste.
    private var light: Brush? = null
    private var lightWindowWidth = 0f
    private var lightBuiltWith = Color.Unspecified

    override fun ContentDrawScope.draw() {
        drawContent()
        val windowWidth = currentValueOf(LocalWindowInfo).containerSize.width.toFloat()
        val lightWidth = windowWidth * LIGHT_WIDTH_PART
        if (light == null || lightWindowWidth != windowWidth || lightBuiltWith != lightColor) {
            light =
                Brush.horizontalGradient(colors = listOf(Color.Transparent, lightColor, Color.Transparent), startX = 0f, endX = lightWidth)
            lightWindowWidth = windowWidth
            lightBuiltWith = lightColor
        }
        // The light starts fully outside on the left and ends fully outside on the right of the window.
        val lightStartInWindow = -lightWidth + (windowWidth + lightWidth) * sweep.value
        val lightStart = lightStartInWindow - blockLeftInWindow
        translate(left = lightStart) {
            // Drawn in the moved space, so the block's own area is where it was before the move.
            drawRect(brush = checkNotNull(light), topLeft = Offset(-lightStart, 0f), size = size)
        }
    }
}
