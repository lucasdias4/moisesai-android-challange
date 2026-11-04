package com.moisesai.challange.feature.songdetails.presentation.model

import com.moisesai.challange.core.design.model.UiErrorType

/** What the player screen shows: the track is being prepared, it could not play, or it is here to control. */
internal sealed interface UiSongDetailsUiState {

    data object Loading : UiSongDetailsUiState

    data class Error(val type: UiErrorType) : UiSongDetailsUiState

    /**
     * [isTrackOptionsVisible] says whether the sheet with the choices for the track is over the screen.
     * [isCannotPlayMessageVisible] is true from the moment the user taps play on a track that cannot play until the
     * screen has shown the message about it.
     */
    data class Success(
        val playingTrack: UiPlayingTrack,
        val isTrackOptionsVisible: Boolean,
        val isCannotPlayMessageVisible: Boolean,
    ) : UiSongDetailsUiState
}
