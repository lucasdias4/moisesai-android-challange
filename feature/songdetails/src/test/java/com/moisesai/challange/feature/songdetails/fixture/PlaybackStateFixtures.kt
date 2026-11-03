package com.moisesai.challange.feature.songdetails.fixture

import com.moisesai.challange.core.player.PlaybackState

fun aPlayingState(
    positionMillis: Long = 0L,
    durationMillis: Long = 30_000L,
    isRepeatEnabled: Boolean = false,
) = PlaybackState.Playing(LEVITATING_PREVIEW_URL, positionMillis, durationMillis, isRepeatEnabled)

fun aPausedState(
    positionMillis: Long = 0L,
    durationMillis: Long = 30_000L,
) = PlaybackState.Paused(LEVITATING_PREVIEW_URL, positionMillis, durationMillis, isRepeatEnabled = false)

fun anEndedState() = PlaybackState.Ended(LEVITATING_PREVIEW_URL, positionMillis = 30_000L, durationMillis = 30_000L, isRepeatEnabled = false)

fun aFailedState() = PlaybackState.Failed(LEVITATING_PREVIEW_URL)
