package com.moisesai.challange.feature.songs.presentation.composable.animation.content_swap

/** The two halves of a change of content: the old content leaves, then the new content arrives. */
internal enum class ContentSwapPhase(val durationMillis: Int) {
    FadingOut(durationMillis = 500),
    FadingIn(durationMillis = 500),
}
