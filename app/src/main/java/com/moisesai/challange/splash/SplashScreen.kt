package com.moisesai.challange.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.splash.animation.NoteAnimation
import com.moisesai.challange.splash.animation.SplashPhase
import com.moisesai.challange.splash.extension.spotlightBackground
import kotlinx.coroutines.delay

/** The first thing the user sees: the musical note arriving on the spotlight. It calls [onFinished] once, after the last step of [SplashPhase]. */
@Composable
internal fun SplashScreen(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    var phase by remember { mutableStateOf(SplashPhase.Waiting) }
    LaunchedEffect(Unit) {
        SplashPhase.entries.forEach { nextPhase ->
            phase = nextPhase
            delay(nextPhase.durationMillis)
        }
        currentOnFinished()
    }
    Box(modifier = modifier.fillMaxSize().spotlightBackground(), contentAlignment = Alignment.Center) {
        NoteAnimation(phase = phase)
    }
}

@FullPreview
@Composable
private fun SplashScreenPreview() = MoisesaiChallangeTheme { SplashScreen(onFinished = {}) }
