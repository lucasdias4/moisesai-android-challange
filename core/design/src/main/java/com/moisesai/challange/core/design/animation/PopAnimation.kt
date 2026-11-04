package com.moisesai.challange.core.design.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.animation.spec.screenTween

// How small the thing is when it starts to pop in and when it has popped out: 1 = its own size. Small enough to be seen
// growing, big enough not to look like it comes from nowhere.
private const val POP_SCALE = 0.6f

/**
 * Grows its content from a smaller size while it fades in, and shrinks it while it fades out.
 * Used in `HeaderSection`, on the search magnifier: `PopAnimation(isVisible = isSearchButtonVisible) { SearchButton(onClick = onSearchClick) }`.
 */
@Composable
fun PopAnimation(isVisible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = fadeIn(screenTween()) + scaleIn(screenTween(), initialScale = POP_SCALE),
        exit = fadeOut(screenTween()) + scaleOut(screenTween(), targetScale = POP_SCALE),
    ) { content() }
}
