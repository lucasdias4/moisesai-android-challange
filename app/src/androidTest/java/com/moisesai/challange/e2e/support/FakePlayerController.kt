package com.moisesai.challange.e2e.support

import com.moisesai.challange.core.player.PlaybackState
import com.moisesai.challange.core.player.PlayerController
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A player that makes no sound: it plays at once and keeps the audio addresses it was given, so a test can read them. */
@Singleton
class FakePlayerController @Inject constructor() : PlayerController {

    private val state = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = state.asStateFlow()

    val playedAudioUrls = mutableListOf<String>()

    override fun playFromStart(audioUrl: String) {
        playedAudioUrls += audioUrl
        state.value = PlaybackState.Playing(audioUrl, positionMillis = 0, durationMillis = AUDIO_LENGTH_MILLIS, isRepeatEnabled = false)
    }

    override fun pause() {
        val playing = state.value as? PlaybackState.Playing ?: return
        state.value = PlaybackState.Paused(playing.audioUrl, playing.positionMillis, playing.durationMillis, playing.isRepeatEnabled)
    }

    override fun resume() {
        val paused = state.value as? PlaybackState.Paused ?: return
        state.value = PlaybackState.Playing(paused.audioUrl, paused.positionMillis, paused.durationMillis, paused.isRepeatEnabled)
    }

    override fun seekTo(positionMillis: Long) = Unit

    override fun toggleRepeat() = Unit

    override fun stop() {
        state.value = PlaybackState.Idle
    }

    private companion object {
        // The length of the preview clips of iTunes.
        const val AUDIO_LENGTH_MILLIS = 30_000L
    }
}
