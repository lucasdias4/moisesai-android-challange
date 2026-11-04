package com.moisesai.challange.feature.songdetails.presentation.mapper

import com.moisesai.challange.core.design.extension.mainArtist
import com.moisesai.challange.core.design.extension.toLargeCoverUrl
import com.moisesai.challange.core.player.PlaybackState
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60L
private const val CLOCK_SECOND_DIGITS = 2
private const val NO_MILLIS = 0L
private const val NOTHING_PLAYED = 0f
private const val EVERYTHING_PLAYED = 1f
private const val REMAINING_TIME_PREFIX = "-"

// The names and the cover come from the domain track; the times come from the player, not from the domain.
// It runs every 250 ms while the audio plays: the player publishes a new state at that pace, the ViewModel collects it
// and calls this. It also runs on play, pause, seek and end.
internal fun TrackDomainModel.toUiPlayingTrack(playbackState: PlaybackState) = UiPlayingTrack(
    id = id,
    trackName = name,
    artistName = artistName.mainArtist(),
    albumId = albumId,
    artUrl = artUrl?.toLargeCoverUrl(),
    thumbnailArtUrl = artUrl,
    isPlaying = playbackState is PlaybackState.Playing,
    canPlay = playbackState !is PlaybackState.Failed,
    isRepeatEnabled = playbackState.isRepeatEnabledOrFalse,
    progress = playbackState.progress(),
    playedTimeText = playbackState.playedTimeText(),
    remainingTimeText = playbackState.remainingTimeText(),
)

// Until an audio is loaded there is no position, no length and nothing to repeat.
private val PlaybackState.positionMillisOrZero: Long get() = (this as? PlaybackState.Loaded)?.positionMillis ?: NO_MILLIS
private val PlaybackState.durationMillisOrZero: Long get() = (this as? PlaybackState.Loaded)?.durationMillis ?: NO_MILLIS
private val PlaybackState.isRepeatEnabledOrFalse: Boolean get() = (this as? PlaybackState.Loaded)?.isRepeatEnabled ?: false

// Until the length of the audio is known there is nothing to divide by, and nothing has played.
private fun PlaybackState.progress(): Float {
    val isDurationKnown = durationMillisOrZero > NO_MILLIS
    if (!isDurationKnown) return NOTHING_PLAYED
    return (positionMillisOrZero.toFloat() / durationMillisOrZero).coerceIn(NOTHING_PLAYED, EVERYTHING_PLAYED)
}

// The time that has passed is cut down to the second it is in, like a clock.
private fun PlaybackState.playedTimeText(): String = (positionMillisOrZero / MILLIS_PER_SECOND).secondsToClockText()

// The time that is left is rounded up, so "-0:00" appears only at the very end.
private fun PlaybackState.remainingTimeText(): String {
    val remainingMillis = (durationMillisOrZero - positionMillisOrZero).coerceAtLeast(NO_MILLIS)
    return REMAINING_TIME_PREFIX + remainingMillis.roundUpToSeconds().secondsToClockText()
}

private fun Long.roundUpToSeconds(): Long = (this + MILLIS_PER_SECOND - 1) / MILLIS_PER_SECOND

// Built by hand, not with String.format, so the digits never depend on the device language.
private fun Long.secondsToClockText(): String {
    val minutes = this / SECONDS_PER_MINUTE
    val seconds = (this % SECONDS_PER_MINUTE).toString().padStart(CLOCK_SECOND_DIGITS, '0')
    return "$minutes:$seconds"
}
