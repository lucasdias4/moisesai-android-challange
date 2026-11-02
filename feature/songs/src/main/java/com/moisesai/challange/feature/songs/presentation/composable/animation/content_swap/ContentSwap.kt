package com.moisesai.challange.feature.songs.presentation.composable.animation.content_swap

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * What the screen shows while it changes from one kind of content to another. [displayed] is the content to draw,
 * which stays the old one until it has faded out; [alpha] is how visible it is.
 */
@Stable
internal class ContentSwap<T>(initialContent: T) {
    var displayed: T by mutableStateOf(initialContent)
        internal set

    // 0 = invisible, 1 = fully visible. Read while drawing, so the fade never recomposes the screen.
    internal val fade = Animatable(0f)

    val alpha: Float get() = fade.value
}
