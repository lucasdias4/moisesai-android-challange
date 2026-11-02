package com.moisesai.challange.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import com.moisesai.challange.splash.animation.AuroraGlowAnimation
import com.moisesai.challange.splash.animation.EqualizerBarsAnimation
import com.moisesai.challange.splash.animation.SplashPhase
import com.moisesai.challange.splash.animation.TitleAnimation
import com.moisesai.challange.splash.animation.splashExitAnimation
import kotlinx.coroutines.delay

/**
 * Equalizer bars that bounce and collapse while an aurora glow grows, then the
 * title springs in and everything fades out. Calls [onFinished] once, after the last step.
 */
@Composable
internal fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val phase = rememberSplashPhase(onFinished)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.splashExitAnimation(phase),
            contentAlignment = Alignment.Center,
        ) {
            AuroraGlowAnimation(phase = phase)
            EqualizerBarsAnimation(phase = phase)
            TitleAnimation(phase = phase)
        }
    }
}

@Composable
private fun rememberSplashPhase(onFinished: () -> Unit): SplashPhase {
    var phase by remember { mutableStateOf(SplashPhase.entries.first()) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        SplashPhase.entries.forEach { nextPhase ->
            phase = nextPhase
            delay(nextPhase.durationMillis)
        }
        currentOnFinished()
    }
    return phase
}

@FullPreview
@Composable
private fun SplashScreenPreview() {
    MoisesaiChallangeTheme {
        SplashScreen(onFinished = {})
    }
}
