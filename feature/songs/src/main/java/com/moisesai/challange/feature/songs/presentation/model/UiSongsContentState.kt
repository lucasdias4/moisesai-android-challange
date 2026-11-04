package com.moisesai.challange.feature.songs.presentation.model

/**
 * What the Songs screen is showing, with only the data that look needs. `SongsListSection` decides what the list
 * draws for each one.
 */
internal sealed interface UiSongsContentState {

    /** No search is active: the screen shows the invitation to search or the history. */
    sealed interface Home : UiSongsContentState

    /** A search is active: the screen shows how it is going. */
    sealed interface Search : UiSongsContentState

    /** Nothing was ever played or searched: invitations to search, suggested songs and the placeholder for the history. */
    data class FirstLaunch(val suggestions: UiSuggestionsState) : Home

    /** Nothing is searched and something was played before: the history, the latest first. */
    data class History(val recents: List<UiTrack>) : Home

    /** A search is running and has nothing to show yet. */
    data object SearchLoading : Search

    /** A search failed and there are no results to keep on screen. */
    data class SearchError(val error: UiSongsError) : Search

    /** A search worked and found nothing for [searchText]. */
    data class SearchNoResults(val searchText: String) : Search

    /** A search found [tracks] (at least one). [status] says what the list is doing besides showing them. */
    data class SearchResults(val tracks: List<UiTrack>, val status: UiSearchResultsState) : Search
}
