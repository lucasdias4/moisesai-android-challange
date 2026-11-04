package com.moisesai.challange.feature.songdetails.presentation.model

/**
 * The track the player shows: its names, its cover, and how far it has played. [progress] goes from 0 (just started)
 * to 1 (ended); [playedTimeText] and [remainingTimeText] are already formatted, like "1:26" and "-2:54". [canPlay] is
 * false while the audio could not be loaded, for example without a connection: the track is still shown, it just
 * cannot be played.
 */
internal data class UiPlayingTrack(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val albumId: Long,
    val artUrl: String?,
    /** The small picture of the same cover that the lists already show: it is on screen while [artUrl], the big one, loads. */
    val thumbnailArtUrl: String?,
    val isPlaying: Boolean,
    val canPlay: Boolean,
    val isRepeatEnabled: Boolean,
    val progress: Float,
    val playedTimeText: String,
    val remainingTimeText: String,
)
