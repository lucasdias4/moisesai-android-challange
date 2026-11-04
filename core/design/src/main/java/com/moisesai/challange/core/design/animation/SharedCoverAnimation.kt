package com.moisesai.challange.core.design.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Makes the cover of track [trackId] fly from one screen to the next, growing or shrinking on the way, instead of fading out and in.
 * Used in `SongRowComponent` and in the player's `CoverSection`, around the cover: `SharedCoverAnimation(trackId = track.id) { CoverImageComponent(...) }`.
 */
@Composable
fun SharedCoverAnimation(trackId: Long, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    val coverModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "cover-$trackId"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ -> screenTween() },
            )
        }
    } else {
        modifier
    }
    Box(modifier = coverModifier) { content() }
}

/** Where the covers fly from one screen to the next: the app provides it around its navigation. Without it (a preview, a test) a cover just stays where it is. */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/** The screen that is on its way in or out: each screen of the navigation provides its own. */
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
