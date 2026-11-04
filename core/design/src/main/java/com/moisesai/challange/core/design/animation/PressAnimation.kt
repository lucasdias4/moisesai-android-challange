package com.moisesai.challange.core.design.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// How small a pressed thing gets: 1 = its own size. A little less than 1 feels like pushing it in, without moving the layout.
private const val PRESSED_SCALE = 0.96f

/**
 * Works like `clickable`, but the thing shrinks a little while the finger is on it and springs back when it leaves.
 * Used in `SongRowComponent`, instead of `clickable`: `Modifier.weight(1f).pressAnimation(onClick = onClick).clip(MaterialTheme.shapes.small)`.
 */
fun Modifier.pressAnimation(role: Role? = null, onClick: () -> Unit): Modifier =
    clickable(interactionSource = null, indication = PressIndication, role = role, onClick = onClick)

private object PressIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = PressNode(interactionSource)

    override fun hashCode() = -1

    override fun equals(other: Any?) = other === this
}

private class PressNode(private val interactionSource: InteractionSource) :
    Modifier.Node(),
    DrawModifierNode {

    // 1 = normal size, PRESSED_SCALE = fully pressed. Read only while drawing.
    private val scale = Animatable(1f)

    override fun onAttach() {
        coroutineScope.launch {
            // The latest touch wins: a quick tap must not wait for the previous move to end.
            interactionSource.interactions.collectLatest { interaction ->
                val target = if (interaction is PressInteraction.Press) PRESSED_SCALE else 1f
                scale.animateTo(target, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium))
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(scale.value) { this@draw.drawContent() }
    }
}
