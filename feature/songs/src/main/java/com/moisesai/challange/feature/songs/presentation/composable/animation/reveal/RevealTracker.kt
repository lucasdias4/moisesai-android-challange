package com.moisesai.challange.feature.songs.presentation.composable.animation.reveal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * Remembers which cards and rows have already arrived. A list throws away what scrolls out of sight and builds it
 * again when it scrolls back, and without this memory every row would arrive a second time each time it came back.
 * It is open only so that previews can show items already in place (`AllRevealedTracker`).
 */
@Stable
internal open class RevealTracker {
    private val revealedKeys = HashSet<Any>()

    open fun hasRevealed(key: Any): Boolean = key in revealedKeys

    fun markRevealed(key: Any) {
        revealedKeys += key
    }
}

/** A new tracker for every [contentKey]: when the screen shows something else, its items arrive again. */
@Composable
internal fun rememberRevealTracker(contentKey: Any): RevealTracker = remember(contentKey) { RevealTracker() }
