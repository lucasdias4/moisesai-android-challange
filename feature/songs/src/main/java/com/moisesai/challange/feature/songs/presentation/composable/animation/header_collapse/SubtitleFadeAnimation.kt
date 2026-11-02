package com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.moisesai.challange.core.design.animation.CssEaseEasing
import kotlinx.coroutines.flow.collectLatest

// The subtitle fades faster than the title and is already gone by 71% of the way.
private const val SUBTITLE_FADE_RATE = 1.4f

// The subtitle's fade trails the scroll by this long: it follows the finger instead of being tied to it.
private const val SUBTITLE_FOLLOW_MILLIS = 400

/** How visible the subtitle is: it follows the scroll but a little late, like the design. Read while drawing. */
@Composable
internal fun rememberSubtitleAlpha(collapseProgress: () -> Float): State<Float> {
    // 1 = fully visible, 0 = gone.
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        snapshotFlow { (1f - SUBTITLE_FADE_RATE * collapseProgress()).coerceIn(0f, 1f) }
            .collectLatest { target -> alpha.animateTo(target, tween(SUBTITLE_FOLLOW_MILLIS, easing = CssEaseEasing)) }
    }
    return alpha.asState()
}
