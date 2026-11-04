package com.moisesai.challange.feature.songdetails.presentation.composable.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.strings.R

// Swapping one icon for another is a small change, so it is quicker than a change of screen.
private const val SWAP_MILLIS = 180

// The icon that leaves shrinks to this size while it fades, and the one that arrives grows from it: 1 = its own size.
private const val HIDDEN_SCALE = 0.6f

/**
 * Swaps the play icon and the pause icon when the music starts or stops: one shrinks and fades away while the other grows in.
 * Used in `PlayPauseButtonComponent`, as the button's icon: `PlayPauseIconAnimation(isPlaying = playingTrack.isPlaying, tint = iconColor, ...)`.
 */
@Composable
internal fun PlayPauseIconAnimation(isPlaying: Boolean, tint: Color, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = isPlaying,
        modifier = modifier,
        transitionSpec = {
            (fadeIn(tween(SWAP_MILLIS)) + scaleIn(tween(SWAP_MILLIS), initialScale = HIDDEN_SCALE)) togetherWith
                (fadeOut(tween(SWAP_MILLIS)) + scaleOut(tween(SWAP_MILLIS), targetScale = HIDDEN_SCALE))
        },
        label = "playPauseIcon",
    ) { playing ->
        Icon(
            painter = painterResource(if (playing) DesignR.drawable.ic_pause else DesignR.drawable.ic_play),
            contentDescription = stringResource(if (playing) R.string.songdetails_pause else R.string.songdetails_play),
            modifier = Modifier.fillMaxSize(),
            tint = tint,
        )
    }
}
