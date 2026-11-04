package com.moisesai.challange.core.design.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-fades when the kind of state changes, such as a skeleton that becomes the real screen, and not on updates of the same kind.
 * Used in `AlbumScreen`: `StateAnimation(state = uiState) { state -> when (state) { Loading -> AlbumLayout(skeleton...) ; is Success -> AlbumLayout(...) } }`.
 */
@Composable
fun <T : Any> StateAnimation(state: T, modifier: Modifier = Modifier, content: @Composable (T) -> Unit) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = { fadeIn(screenTween()) togetherWith fadeOut(screenTween()) },
        // Only the kind counts: a new value of the same kind (a new time on the player, every 250 ms) updates the content with no transition.
        contentKey = { currentState -> currentState::class },
        label = "stateAnimation",
    ) { currentState -> content(currentState) }
}
