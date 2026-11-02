package com.moisesai.challange.splash.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay

// Each letter waits 60 ms more than the one before it.
private const val STAGGER_MILLIS = 60L
// A letter starts stretched to 1.8x its height and 48 dp too low, then springs into place.
private const val ENTRY_START_SCALE_Y = 1.8f
private val EntryStartOffsetY = 48.dp

/**
 * One letter that springs up into place [letterIndex] * a short delay after
 * [phase] becomes [SplashPhase.Revealing], so the letters arrive in sequence.
 */
@Composable
internal fun TitleLetterAnimation(
    letter: Char,
    color: Color,
    shadow: Shadow?,
    letterIndex: Int,
    phase: SplashPhase,
    modifier: Modifier = Modifier,
) {
    // Flips to true once, after this letter's own delay. The spring below reacts to it.
    var hasEntered by remember { mutableStateOf(false) }
    LaunchedEffect(phase) {
        if (phase == SplashPhase.Revealing) {
            // The delay grows with the letter's position: that is what makes them arrive in sequence.
            delay(letterIndex * STAGGER_MILLIS)
            hasEntered = true
        }
    }
    // 0 = not entered, 1 = in place. The bouncy spring overshoots 1, so the letter
    // passes its spot and settles back, which gives the small rebound.
    val entryProgress by animateFloatAsState(
        targetValue = if (hasEntered) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "entryProgress",
    )

    Text(
        text = letter.toString(),
        color = color,
        style = MaterialTheme.typography.displayLarge.copy(shadow = shadow),
        // graphicsLayer animates without recomposing. alpha is clamped because progress can pass 1.
        modifier = modifier.graphicsLayer {
            alpha = entryProgress.coerceIn(0f, 1f)
            translationY = (1f - entryProgress) * EntryStartOffsetY.toPx()
            scaleY = lerp(ENTRY_START_SCALE_Y, 1f, entryProgress)
        },
    )
}
