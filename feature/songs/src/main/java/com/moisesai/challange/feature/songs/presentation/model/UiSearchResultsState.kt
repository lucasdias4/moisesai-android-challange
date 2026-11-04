package com.moisesai.challange.feature.songs.presentation.model

/** What a list of search results is doing besides showing the songs. Only one of these is true at a time. */
internal sealed interface UiSearchResultsState {

    /** Idle, and another page can be asked for when the user nears the end. */
    data object HasMore : UiSearchResultsState

    /** Idle, and the last page is already shown. */
    data object Complete : UiSearchResultsState

    /** The next page is on its way: a loader is drawn below the songs. */
    data object LoadingMore : UiSearchResultsState

    /** The search is being asked for again: the songs stay while the pull-to-refresh indicator spins. */
    data object Refreshing : UiSearchResultsState

    /** The last request failed: the songs stay, the error is drawn above them and no page is asked for until a refresh. */
    data class Failed(val error: UiSongsError) : UiSearchResultsState
}
