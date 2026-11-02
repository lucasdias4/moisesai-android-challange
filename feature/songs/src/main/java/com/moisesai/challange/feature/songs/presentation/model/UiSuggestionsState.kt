package com.moisesai.challange.feature.songs.presentation.model


/** The "Start here" songs of the empty home. They arrive after a lookup, and may not arrive at all (offline, first launch). */
internal sealed interface UiSuggestionsState {

    /** Still being looked up: nothing is drawn yet, so the cards and the placeholder can appear together. */
    data object Loading : UiSuggestionsState

    data class Loaded(val tracks: List<UiTrack>) : UiSuggestionsState

    /** Could not be found: only the placeholder card is shown. */
    data object Unavailable : UiSuggestionsState
}
