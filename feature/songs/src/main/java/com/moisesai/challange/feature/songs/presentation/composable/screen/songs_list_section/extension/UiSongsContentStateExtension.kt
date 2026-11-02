package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

/** True while the results are being asked for again, which is when the pull-to-refresh indicator spins. */
internal fun UiSongsContentState.Search.isRefreshing() =
    this is UiSongsContentState.SearchResults && status == UiSearchResultsState.Refreshing
