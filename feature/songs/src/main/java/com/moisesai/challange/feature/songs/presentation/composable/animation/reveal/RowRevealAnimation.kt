package com.moisesai.challange.feature.songs.presentation.composable.animation.reveal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.CssEaseEasing
import com.moisesai.challange.core.design.animation.SpringOvershootEasing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Only the rows that fit on a screen arrive one after another (about 10). Rows further down are scrolled into
// view by the user, so they just appear.
private const val MAX_REVEALED_ROWS = 10

// Each row starts this much later than the one above it, so the list seems to pour in from the top.
private const val ROW_STAGGER_MILLIS = 90L

// The rise is slow and springy, the fade is quicker, so the row is visible before it has finished moving.
private const val ROW_RISE_MILLIS = 700
private const val ROW_FADE_MILLIS = 500

// How far below its place a row starts, before it rises into it.
private val RowRiseDistance = 24.dp

/** How long row number [rowIndex] waits before it arrives, or null when it should not be animated at all. */
internal fun rowRevealDelayMillis(rowIndex: Int): Long? =
    if (rowIndex < MAX_REVEALED_ROWS) rowIndex * ROW_STAGGER_MILLIS else null

/** The row fades in while it rises into place, [rowRevealDelayMillis] after the list appears. */
@Composable
internal fun Modifier.rowRevealAnimation(revealKey: Any, rowIndex: Int, tracker: RevealTracker): Modifier {
    // No delay means no animation: the row is too far down the list, or it has already arrived once.
    val delayMillis = rowRevealDelayMillis(rowIndex).takeUnless { tracker.hasRevealed(revealKey) }
    val startsHidden = delayMillis != null
    // 0 = low and hidden, 1 = in place. The springy rise takes the row a little past 1 and back.
    val rise = remember(revealKey) { Animatable(if (startsHidden) 0f else 1f) }
    val fade = remember(revealKey) { Animatable(if (startsHidden) 0f else 1f) }
    LaunchedEffect(revealKey) {
        if (delayMillis == null) return@LaunchedEffect
        delay(delayMillis)
        launch { fade.animateTo(1f, tween(ROW_FADE_MILLIS, easing = CssEaseEasing)) }
        rise.animateTo(1f, tween(ROW_RISE_MILLIS, easing = SpringOvershootEasing))
        tracker.markRevealed(revealKey)
    }
    // Read while drawing, so a frame of the arrival redraws the row and never recomposes the list.
    return graphicsLayer {
        alpha = fade.value.coerceIn(0f, 1f)
        translationY = (1f - rise.value) * RowRiseDistance.toPx()
    }
}
