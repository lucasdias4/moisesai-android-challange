package com.moisesai.challange.feature.songdetails.presentation.support

import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks

/** Keeps what the user did on the screen, so a test can say what was reported. */
internal class RecordingSongDetailsCallbacks {
    var backCount = 0
    var optionsClickCount = 0
    var optionsDismissCount = 0
    val albumIds = mutableListOf<Long>()
    var playPauseCount = 0
    val seekProgresses = mutableListOf<Float>()
    var previousCount = 0
    var nextCount = 0
    var repeatCount = 0
    var retryCount = 0
    var cannotPlayShownCount = 0

    fun asSongDetailsCallbacks() = SongDetailsCallbacks(
        onBackClick = { backCount++ },
        onTrackOptionsClick = { optionsClickCount++ },
        onTrackOptionsDismiss = { optionsDismissCount++ },
        onViewAlbumClick = { albumIds += it },
        onPlayPauseClick = { playPauseCount++ },
        onTrackProgressBarChangedByUser = { seekProgresses += it },
        onPreviousClick = { previousCount++ },
        onNextClick = { nextCount++ },
        onRepeatClick = { repeatCount++ },
        onRetry = { retryCount++ },
        onCannotPlayMessageShown = { cannotPlayShownCount++ },
    )
}
