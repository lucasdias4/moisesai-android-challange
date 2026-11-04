package com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper

import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

// The track of the design, a quarter of the way through. The cover is empty: a preview has no network.
internal val SamplePlayingTrack = UiPlayingTrack(
    id = 1L,
    trackName = "Get Lucky",
    artistName = "Daft Punk feat. Pharrell Williams",
    albumId = 1L,
    artUrl = null,
    thumbnailArtUrl = null,
    isPlaying = false,
    canPlay = true,
    isRepeatEnabled = false,
    progress = 0.25f,
    playedTimeText = "1:26",
    remainingTimeText = "-2:54",
)
