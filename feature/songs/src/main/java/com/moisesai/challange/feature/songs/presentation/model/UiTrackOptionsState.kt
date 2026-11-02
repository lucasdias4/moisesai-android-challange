package com.moisesai.challange.feature.songs.presentation.model

/** Whether the sheet with the choices for a song is on screen, and for which song. */
internal sealed interface UiTrackOptionsState {

    /** No sheet is shown. */
    data object Hidden : UiTrackOptionsState

    /** The sheet is shown for [track]. */
    data class Visible(val track: UiTrack) : UiTrackOptionsState
}
