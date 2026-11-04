package com.moisesai.challange.core.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// ExoPlayer tells when the position jumps but not while it moves, so while playing the position is read this often.
// Four times a second keeps a seek bar and a clock that show seconds looking smooth.
private const val POSITION_REFRESH_MILLIS = 250L
private const val AUDIO_START_MILLIS = 0L
private const val UNKNOWN_DURATION_MILLIS = 0L

// Telling Android that this is music lets it lower or pause the audio for a call or a notification, and give it back after.
private val MusicAudioAttributes = AudioAttributes.Builder()
    .setUsage(C.USAGE_MEDIA)
    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
    .build()
private const val HANDLE_AUDIO_FOCUS = true

/** One ExoPlayer for the whole app, so two screens can never play two audios at once. */
@Singleton
internal class ExoPlayerController @Inject constructor(@ApplicationContext context: Context) : PlayerController {

    // Built on the main thread, where Hilt creates it for the first ViewModel that needs it, so ExoPlayer is used from there.
    private val exoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(MusicAudioAttributes, HANDLE_AUDIO_FOCUS)
        .setHandleAudioBecomingNoisy(true)
        .build()
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var positionRefreshJob: Job? = null

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    init {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    updatePlaybackState()
                    if (exoPlayer.isPlaying) {
                        startRefreshingPosition()
                    } else {
                        stopRefreshingPosition()
                    }
                }
            },
        )
    }

    override fun playFromStart(audioUrl: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(audioUrl))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        val hasEnded = exoPlayer.playbackState == Player.STATE_ENDED
        if (hasEnded) {
            exoPlayer.seekTo(AUDIO_START_MILLIS)
        }
        exoPlayer.play()
    }

    override fun seekTo(positionMillis: Long) {
        exoPlayer.seekTo(positionMillis)
    }

    override fun toggleRepeat() {
        val isRepeatEnabled = exoPlayer.repeatMode == Player.REPEAT_MODE_ONE
        exoPlayer.repeatMode = if (isRepeatEnabled) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ONE
        }
    }

    override fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    private fun startRefreshingPosition() {
        val isAlreadyRefreshing = positionRefreshJob?.isActive == true
        if (isAlreadyRefreshing) return
        positionRefreshJob = mainScope.launch {
            while (isActive) {
                updatePlaybackState()
                delay(POSITION_REFRESH_MILLIS.milliseconds)
            }
        }
    }

    private fun stopRefreshingPosition() {
        positionRefreshJob?.cancel()
    }

    private fun updatePlaybackState() {
        _playbackState.value = readPlaybackState()
    }

    private fun readPlaybackState(): PlaybackState {
        val audioUrl = exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString()
            ?: return PlaybackState.Idle
        // ExoPlayer keeps its error until the next audio is prepared, which is when the state stops being failed.
        if (exoPlayer.playerError != null) {
            return PlaybackState.Failed(audioUrl)
        }
        val positionMillis = exoPlayer.currentPosition
        // ExoPlayer has no duration until the audio is loaded.
        val durationMillis =
            exoPlayer.duration.takeUnless { it == C.TIME_UNSET } ?: UNKNOWN_DURATION_MILLIS
        val isRepeatEnabled = exoPlayer.repeatMode == Player.REPEAT_MODE_ONE
        return when {
            exoPlayer.playbackState == Player.STATE_ENDED -> PlaybackState.Ended(
                audioUrl,
                positionMillis,
                durationMillis,
                isRepeatEnabled,
            )

            exoPlayer.isPlaying -> PlaybackState.Playing(
                audioUrl,
                positionMillis,
                durationMillis,
                isRepeatEnabled,
            )

            else -> PlaybackState.Paused(audioUrl, positionMillis, durationMillis, isRepeatEnabled)
        }
    }
}
