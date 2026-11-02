package com.moisesai.challange.feature.songs.presentation.composable.animation.content_swap

import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.moisesai.challange.core.design.animation.CssEaseEasing

/**
 * Shows [content], and when [contentKeyOf] gives it another key, fades the old content out, switches and fades the
 * new content in. A change of [content] that keeps the same key shows at once, because it is the same content
 * updating (results arriving) and not a different one.
 */
@Composable
internal fun <T> rememberContentSwap(content: T, contentKeyOf: (T) -> Any): ContentSwap<T> {
    val swap = remember { ContentSwap(content) }
    val latestContent by rememberUpdatedState(content)
    val contentKey = contentKeyOf(content)
    var displayedKey by remember { mutableStateOf(contentKey) }

    // Another kind of content: the old one leaves, then the new one arrives. The very first content, or a change
    // back to what is displayed, only has to be made visible.
    LaunchedEffect(contentKey) {
        if (contentKey != displayedKey) {
            swap.fadeOut()
            displayedKey = contentKey
            swap.displayed = latestContent
        }
        swap.fadeIn()
    }
    // The same kind of content, updated: shown at once.
    LaunchedEffect(content) {
        if (contentKeyOf(content) == displayedKey) swap.displayed = content
    }
    return swap
}

/** Makes this part of the screen as visible as the content swap says. */
internal fun <T> Modifier.contentSwapAlpha(swap: ContentSwap<T>): Modifier = graphicsLayer { alpha = swap.alpha }

private suspend fun ContentSwap<*>.fadeOut() {
    fade.animateTo(0f, tween(ContentSwapPhase.FadingOut.durationMillis, easing = CssEaseEasing))
}

private suspend fun ContentSwap<*>.fadeIn() {
    fade.animateTo(1f, tween(ContentSwapPhase.FadingIn.durationMillis, easing = CssEaseEasing))
}
