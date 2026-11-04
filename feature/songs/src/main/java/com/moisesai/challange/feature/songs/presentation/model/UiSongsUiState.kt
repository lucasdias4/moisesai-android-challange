package com.moisesai.challange.feature.songs.presentation.model

internal data class UiSongsUiState(
    /** What is typed in the search field, even before the search for it has started. */
    val query: String,
    /**
     * False after a search was asked for: the field is tucked away so the results get the room, and the header shows a
     * magnifier that brings it back.
     */
    val isSearchFieldVisible: Boolean,
    val content: UiSongsContentState,
    val trackOptions: UiTrackOptionsState,
)
