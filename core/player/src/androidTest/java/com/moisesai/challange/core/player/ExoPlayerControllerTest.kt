package com.moisesai.challange.core.player

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExoPlayerControllerTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var controller: ExoPlayerController

    @Before
    fun createController() {
        // ExoPlayer belongs to the main thread, where Hilt builds the controller in the app.
        onMainThread { controller = ExoPlayerController(instrumentation.targetContext) }
    }

    @Test
    fun GIVEN_nothing_was_played_WHEN_reading_the_state_THEN_it_is_idle() {
        assertEquals(PlaybackState.Idle, controller.playbackState.value)
    }

    @Test
    fun GIVEN_a_playable_audio_WHEN_it_is_played_from_the_start_THEN_it_plays_with_its_length_known() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }

        val playing = awaitState<PlaybackState.Playing>()

        assertEquals(LONG_AUDIO, playing.audioUrl)
        assertTrue(playing.durationMillis in LONG_AUDIO_MILLIS - LENGTH_TOLERANCE_MILLIS..LONG_AUDIO_MILLIS + LENGTH_TOLERANCE_MILLIS)
    }

    @Test
    fun GIVEN_a_short_audio_WHEN_it_plays_to_its_end_THEN_the_state_is_ended() {
        onMainThread { controller.playFromStart(SHORT_AUDIO) }

        val ended = awaitState<PlaybackState.Ended>()

        assertEquals(SHORT_AUDIO, ended.audioUrl)
    }

    @Test
    fun GIVEN_an_audio_that_plays_WHEN_it_is_paused_and_resumed_THEN_the_state_is_paused_and_then_playing_again() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }
        awaitState<PlaybackState.Playing>()

        onMainThread { controller.pause() }
        awaitState<PlaybackState.Paused>()
        onMainThread { controller.resume() }

        awaitState<PlaybackState.Playing>()
    }

    @Test
    fun GIVEN_a_paused_audio_WHEN_seeking_to_two_seconds_THEN_the_position_is_two_seconds() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }
        awaitState<PlaybackState.Playing>()
        onMainThread { controller.pause() }
        awaitState<PlaybackState.Paused>()

        onMainThread { controller.seekTo(TWO_SECONDS_MILLIS) }

        val paused = awaitState<PlaybackState.Paused> { it.positionMillis >= TWO_SECONDS_MILLIS }
        assertTrue(paused.positionMillis in TWO_SECONDS_MILLIS..TWO_SECONDS_MILLIS + LENGTH_TOLERANCE_MILLIS)
    }

    @Test
    fun GIVEN_an_audio_that_plays_WHEN_repeat_is_toggled_twice_THEN_the_state_says_repeat_on_and_then_off() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }
        awaitState<PlaybackState.Playing>()

        onMainThread { controller.toggleRepeat() }
        val withRepeat = awaitState<PlaybackState.Loaded> { it.isRepeatEnabled }
        onMainThread { controller.toggleRepeat() }
        val withoutRepeat = awaitState<PlaybackState.Loaded> { !it.isRepeatEnabled }

        assertTrue(withRepeat.isRepeatEnabled)
        assertTrue(!withoutRepeat.isRepeatEnabled)
    }

    @Test
    fun GIVEN_an_audio_that_does_not_exist_WHEN_it_is_played_THEN_the_state_is_failed_for_that_audio() {
        onMainThread { controller.playFromStart(MISSING_AUDIO) }

        val failed = awaitState<PlaybackState.Failed>()

        assertEquals(MISSING_AUDIO, failed.audioUrl)
    }

    @Test
    fun GIVEN_an_audio_that_plays_WHEN_it_is_stopped_THEN_the_state_is_idle() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }
        awaitState<PlaybackState.Playing>()

        onMainThread { controller.stop() }

        awaitState<PlaybackState.Idle>()
    }

    @Test
    fun GIVEN_an_audio_that_plays_WHEN_another_audio_is_played_THEN_the_state_follows_the_new_audio() {
        onMainThread { controller.playFromStart(LONG_AUDIO) }
        awaitState<PlaybackState.Playing>()

        onMainThread { controller.playFromStart(SHORT_AUDIO) }

        val playingTheNewOne = awaitState<PlaybackState.PlaybackStateWithAudio> { it.audioUrl == SHORT_AUDIO }
        assertEquals(SHORT_AUDIO, playingTheNewOne.audioUrl)
    }

    @Test
    fun GIVEN_an_audio_that_ended_WHEN_it_is_resumed_THEN_it_plays_again_from_the_start() {
        onMainThread { controller.playFromStart(SHORT_AUDIO) }
        awaitState<PlaybackState.Ended>()

        onMainThread { controller.resume() }

        awaitState<PlaybackState.Playing>()
    }

    private fun onMainThread(action: () -> Unit) = instrumentation.runOnMainSync(action)

    // Waits for the first state of the given kind that also satisfies [matches]; it fails if none comes in time.
    private inline fun <reified T : PlaybackState> awaitState(crossinline matches: (T) -> Boolean = { true }): T = runBlocking {
        withTimeout(WAIT_MILLIS) {
            controller.playbackState.first { it is T && matches(it) } as T
        }
    }

    private companion object {
        const val LONG_AUDIO = "asset:///tone.wav"
        const val SHORT_AUDIO = "asset:///tone-short.wav"
        const val MISSING_AUDIO = "asset:///missing.wav"
        const val LONG_AUDIO_MILLIS = 3_000L
        const val TWO_SECONDS_MILLIS = 2_000L
        const val LENGTH_TOLERANCE_MILLIS = 400L
        const val WAIT_MILLIS = 10_000L
    }
}
