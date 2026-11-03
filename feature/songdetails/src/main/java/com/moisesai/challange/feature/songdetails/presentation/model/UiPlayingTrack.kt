package com.moisesai.challange.feature.songdetails.presentation.model

/**
 * The track the player shows: its names, its cover, and how far it has played. [progress] goes from 0 (just started)
 * to 1 (ended); [playedTimeText] and [remainingTimeText] are already formatted, like "1:26" and "-2:54".
 */
internal data class UiPlayingTrack(
    val trackName: String,
    val artistName: String,
    val albumId: Long,
    val artUrl: String?,
    val isPlaying: Boolean,
    val isRepeatEnabled: Boolean,
    val progress: Float,
    val playedTimeText: String,
    val remainingTimeText: String,
)
