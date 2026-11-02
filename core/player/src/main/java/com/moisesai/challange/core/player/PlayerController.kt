package com.moisesai.challange.core.player

import kotlinx.coroutines.flow.StateFlow

/** Plays one audio at a time from a url. It knows nothing about tracks, albums or iTunes. */
interface PlayerController {

    /** The current state, emitted again whenever it changes and a few times a second while playing. */
    val playbackState: StateFlow<PlaybackState>

    /** Loads the audio at [audioUrl] and plays it from the start, replacing what was loaded. */
    fun playFromStart(audioUrl: String)

    fun pause()

    /** Continues from where [pause] stopped, or plays again from the start when the audio had ended. */
    fun resume()

    fun seekTo(positionMillis: Long)

    /** Turns repeating the loaded audio on when it is off, and off when it is on. */
    fun toggleRepeat()

    /** Stops playing and forgets the loaded audio. */
    fun stop()
}
