package com.moisesai.challange.feature.songs.presentation.composable.animation.reveal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.moisesai.challange.core.design.animation.SpringOvershootEasing
import kotlinx.coroutines.delay

// A card takes this long to arrive: long enough to see it rise and settle, short enough not to make anyone wait.
private const val CARD_REVEAL_MILLIS = 900

// Each card starts this much later than the one above it, so the cards pour in from the top.
private const val CARD_STAGGER_MILLIS = 120L

// The first card waits a moment after the screen appears, so the header is already there when the cards arrive.
private const val CARD_REVEAL_START_DELAY_MILLIS = 300L

// A card starts this far below its place and a little smaller, then rises and grows into it.
private val CardRiseDistance = 40.dp
private const val CARD_START_SCALE = 0.96f

/** The card, [cardIndex] from the top, fades in while it rises and grows into place, one after another. */
@Composable
internal fun Modifier.cardRevealAnimation(revealKey: Any, cardIndex: Int, tracker: RevealTracker): Modifier {
    val hasArrivedBefore = tracker.hasRevealed(revealKey)
    // 0 = hidden, low and small; 1 = in place. The overshooting curve takes it a little past 1 and back.
    val arrival = remember(revealKey) { Animatable(if (hasArrivedBefore) 1f else 0f) }
    LaunchedEffect(revealKey) {
        if (hasArrivedBefore) return@LaunchedEffect
        delay(CARD_REVEAL_START_DELAY_MILLIS + cardIndex * CARD_STAGGER_MILLIS)
        arrival.animateTo(1f, tween(CARD_REVEAL_MILLIS, easing = SpringOvershootEasing))
        tracker.markRevealed(revealKey)
    }
    // Read while drawing, so a frame of the arrival redraws the card and never recomposes the list.
    return graphicsLayer {
        val progress = arrival.value
        // The overshoot takes the progress past 1, and an opacity cannot go past 1.
        alpha = progress.coerceIn(0f, 1f)
        translationY = (1f - progress) * CardRiseDistance.toPx()
        scaleX = lerp(CARD_START_SCALE, 1f, progress)
        scaleY = scaleX
    }
}
