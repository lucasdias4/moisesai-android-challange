package com.moisesai.challange.feature.songs.presentation.model

internal data class UiSongsUiState(
    /** What is typed in the search field, even before the search for it has started. */
    val query: String,
    val content: UiSongsContentState,
    val trackOptions: UiTrackOptionsState,
)
