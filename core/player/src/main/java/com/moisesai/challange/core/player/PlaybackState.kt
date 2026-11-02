package com.moisesai.challange.core.player

/** What the player is doing right now. Every time is in milliseconds. */
sealed interface PlaybackState {

    /** Nothing is loaded. */
    data object Idle : PlaybackState

    /** A state of an audio that was given to the player: the one that is loaded, or the one that failed. */
    sealed interface PlaybackStateWithAudio : PlaybackState {
        val audioUrl: String
    }

    /** The audio at [audioUrl] could not be loaded or played. It stays failed until another audio is played. */
    data class Failed(override val audioUrl: String) : PlaybackStateWithAudio

    /** An audio is loaded. */
    sealed interface Loaded : PlaybackStateWithAudio {
        val positionMillis: Long

        /** Zero until the length of the audio is known. */
        val durationMillis: Long
        val isRepeatEnabled: Boolean
    }

    /** The audio is playing. */
    data class Playing(
        override val audioUrl: String,
        override val positionMillis: Long,
        override val durationMillis: Long,
        override val isRepeatEnabled: Boolean,
    ) : Loaded

    /** The audio is loaded and not playing: paused, or still waiting for its first sound. */
    data class Paused(
        override val audioUrl: String,
        override val positionMillis: Long,
        override val durationMillis: Long,
        override val isRepeatEnabled: Boolean,
    ) : Loaded

    /** The audio played to its end and, because repeat is off, stopped there. */
    data class Ended(
        override val audioUrl: String,
        override val positionMillis: Long,
        override val durationMillis: Long,
        override val isRepeatEnabled: Boolean,
    ) : Loaded
}
