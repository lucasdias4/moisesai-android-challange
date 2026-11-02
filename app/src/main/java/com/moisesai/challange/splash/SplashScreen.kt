package com.moisesai.challange.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.R
import com.moisesai.challange.splash.extension.spotlightBackground
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import kotlinx.coroutines.delay

// The design keeps the splash on screen for this long before it hands over to the first screen.
private const val SPLASH_DURATION_MILLIS = 800L

// The musical note is 100 x 100 in the design, in the middle of the screen.
private val MusicNoteSize = 100.dp

/** The first thing the user sees: the musical note on the spotlight. It calls [onFinished] once, after it was shown for a moment. */
@Composable
internal fun SplashScreen(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        currentOnFinished()
    }
    Box(modifier = modifier.fillMaxSize().spotlightBackground(), contentAlignment = Alignment.Center) {
        // Decoration only: the splash has no text, so a screen reader has nothing to say about it.
        Image(painter = painterResource(R.drawable.splash_music_note), contentDescription = null, modifier = Modifier.size(MusicNoteSize))
    }
}

@FullPreview
@Composable
private fun SplashScreenPreview() = MoisesaiChallangeTheme { SplashScreen(onFinished = {}) }
