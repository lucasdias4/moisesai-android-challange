package com.moisesai.challange.feature.songdetails.presentation.composable.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

// How big the cover is while the music is stopped: 1 = its full size. A little smaller, so it grows when the music starts.
private const val STOPPED_SCALE = 0.92f

/**
 * Shows its content at full size while the music plays and a little smaller while it is stopped, with a soft spring between the two.
 * Used in `CoverSection`, around the cover: `CoverScaleAnimation(isPlaying = playingTrack.isPlaying) { TrackCoverComponent(...) }`.
 */
@Composable
internal fun CoverScaleAnimation(isPlaying: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // A slow spring with a hint of bounce: the cover settles like something that breathes, not like a button.
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else STOPPED_SCALE,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "coverScale",
    )
    // The scale is read here, while drawing, so each frame redraws the cover and does not recompose it.
    Box(modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }) { content() }
}
