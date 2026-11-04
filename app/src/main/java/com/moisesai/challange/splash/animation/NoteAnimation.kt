package com.moisesai.challange.splash.animation

import com.moisesai.challange.core.design.animation.spec.ScreenEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.R

// The musical note is 100 x 100 in the design, in the middle of the screen.
private val MusicNoteSize = 100.dp

// How big the note is when it starts to appear: 1 = its own size. Close to 1, so it settles instead of flying in.
private const val START_SCALE = 0.8f

/** The musical note: it grows from [START_SCALE] to its size while it fades in, once the splash reaches [SplashPhase.Entering]. */
@Composable
internal fun NoteAnimation(phase: SplashPhase, modifier: Modifier = Modifier) {
    // 0 = hidden and small, 1 = shown at its size.
    val appearance by animateFloatAsState(
        targetValue = if (phase >= SplashPhase.Entering) 1f else 0f,
        animationSpec = tween(SplashPhase.Entering.durationMillis.toInt(), easing = ScreenEasing),
        label = "noteAppearance",
    )
    // Decoration only: the splash has no text, so a screen reader has nothing to say about it.
    Image(
        painter = painterResource(R.drawable.splash_music_note),
        contentDescription = null,
        // The appearance is read here, while drawing, so each frame redraws the note and does not recompose it.
        modifier = modifier.size(MusicNoteSize).graphicsLayer {
            alpha = appearance
            scaleX = START_SCALE + (1f - START_SCALE) * appearance
            scaleY = scaleX
        },
    )
}
