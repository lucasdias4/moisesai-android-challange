package com.moisesai.challange.splash.animation

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.moisesai.challange.core.strings.R

// The accent dot glows in its own colour: strong (80%) and wide (28 dp).
private const val ACCENT_GLOW_ALPHA = 0.8f
private val AccentGlowRadius = 28.dp

/** The app name, one letter at a time. The last character is the accent and glows. */
@Composable
internal fun WordmarkAnimation(
    phase: SplashPhase,
    modifier: Modifier = Modifier,
) {
    // The text comes from strings.xml, so it can be translated or renamed in one place.
    val wordmark = stringResource(R.string.splash_wordmark)
    val accentColor = MaterialTheme.colorScheme.primary
    val accentGlow = Shadow(
        color = accentColor.copy(alpha = ACCENT_GLOW_ALPHA),
        // Shadow wants pixels, so the dp value is converted with the screen density.
        blurRadius = with(LocalDensity.current) { AccentGlowRadius.toPx() },
    )
    val letterColor = MaterialTheme.colorScheme.onBackground

    // Screen readers should read the name once, not letter by letter,
    // so the letters are hidden and the whole word is the description.
    Row(modifier = modifier.clearAndSetSemantics { contentDescription = wordmark }) {
        wordmark.forEachIndexed { index, letter ->
            // The last character (the dot) is the accent.
            val isAccent = index == wordmark.lastIndex
            WordmarkLetterAnimation(
                letter = letter,
                color = if (isAccent) accentColor else letterColor,
                shadow = if (isAccent) accentGlow else null,
                letterIndex = index,
                phase = phase,
            )
        }
    }
}
