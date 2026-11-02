package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.songs.presentation.composable.component.ErrorStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.LoadingStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.NoResultsStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.PartialErrorMessageComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.component.songRowListComponent
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private val ListStartPadding = 24.dp
private val ListEndPadding = 16.dp
private val ListTopPadding = 8.dp

// The design leaves this much room under the last song, for the mini player that will float above the list.
private val ListBottomPadding = 240.dp

// The songs fade out under the search field: black at 80% where they leave, clear 16 further down.
private val TopShadowHeight = 16.dp
private const val TOP_SHADOW_START_ALPHA = 0.8f

// Every item of the list has a key, so the list keeps its scroll position when what it shows changes.
private const val SUGGESTED_SONGS_KEY_PREFIX = "suggested"
private const val RECENT_SONGS_KEY_PREFIX = "recent"
private const val FOUND_SONGS_KEY_PREFIX = "found"
private const val SEARCH_LOADING_KEY = "search-loading"
private const val SEARCH_ERROR_KEY = "search-error"
private const val NO_RESULTS_KEY = "no-results"
private const val UPDATE_FAILED_KEY = "update-failed"
private const val LOADING_MORE_KEY = "loading-more"

/**
 * The songs of the screen, in the way that fits [content]. While a search is shown, pulling the list down
 * asks for the search again.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsListSection(
    content: UiSongsContentState,
    callbacks: SongsCallbacks,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (content is UiSongsContentState.Search) {
            PullToRefreshBox(isRefreshing = content.isRefreshing(), onRefresh = callbacks.onRefresh) {
                SongsColumn(content, callbacks, listState)
            }
        } else {
            SongsColumn(content, callbacks, listState)
        }
        TopShadow(modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun SongsColumn(content: UiSongsContentState, callbacks: SongsCallbacks, listState: LazyListState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = ListStartPadding,
            top = ListTopPadding,
            end = ListEndPadding,
            bottom = ListBottomPadding,
        ),
    ) {
        showItemsFor(content, callbacks)
    }
}

@Composable
private fun TopShadow(modifier: Modifier = Modifier) {
    val black = MaterialTheme.design.colors.background00
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TopShadowHeight)
            .background(Brush.verticalGradient(listOf(black.copy(alpha = TOP_SHADOW_START_ALPHA), black.copy(alpha = 0f)))),
    )
}

/** The one place that says what the list shows for each state of the screen. */
private fun LazyListScope.showItemsFor(content: UiSongsContentState, callbacks: SongsCallbacks) {
    when (content) {
        is UiSongsContentState.FirstLaunch -> showSuggestedSongs(content.suggestions, callbacks)
        is UiSongsContentState.History -> showRecentSongs(content.recents, callbacks)
        UiSongsContentState.SearchLoading -> showSearchLoading()
        is UiSongsContentState.SearchError -> showSearchError(content.error, callbacks)
        is UiSongsContentState.SearchNoResults -> showNoResults(content.searchText)
        is UiSongsContentState.SearchResults -> showFoundSongs(content, callbacks)
    }
}

// Nothing was played yet: the songs we suggest, once they are found. While they load, or if there are none, nothing is drawn.
private fun LazyListScope.showSuggestedSongs(suggestions: UiSuggestionsState, callbacks: SongsCallbacks) {
    val loadedSuggestions = suggestions as? UiSuggestionsState.Loaded ?: return
    songRowListComponent(loadedSuggestions.tracks, SUGGESTED_SONGS_KEY_PREFIX, callbacks)
}

// Something was played before: the recent songs, the latest first.
private fun LazyListScope.showRecentSongs(recentTracks: List<UiTrack>, callbacks: SongsCallbacks) {
    songRowListComponent(recentTracks, RECENT_SONGS_KEY_PREFIX, callbacks)
}

// A search is running and nothing is known yet: a loader where the songs will be.
private fun LazyListScope.showSearchLoading() {
    item(key = SEARCH_LOADING_KEY) { LoadingStateComponent() }
}

// A search failed and there is nothing else to show: what went wrong and a way to try again.
private fun LazyListScope.showSearchError(error: UiSongsError, callbacks: SongsCallbacks) {
    item(key = SEARCH_ERROR_KEY) { ErrorStateComponent(error = error, onRetry = callbacks.onRetry) }
}

// A search worked and found nothing: says so and shows what was searched for.
private fun LazyListScope.showNoResults(searchText: String) {
    item(key = NO_RESULTS_KEY) { NoResultsStateComponent(searchText = searchText) }
}

// A search found songs: one row for each, with a message above them when an update failed and a loader below them
// while the next page arrives.
private fun LazyListScope.showFoundSongs(results: UiSongsContentState.SearchResults, callbacks: SongsCallbacks) {
    if (results.status is UiSearchResultsState.Failed) item(key = UPDATE_FAILED_KEY) { PartialErrorMessageComponent() }
    songRowListComponent(results.tracks, FOUND_SONGS_KEY_PREFIX, callbacks)
    if (results.status == UiSearchResultsState.LoadingMore) item(key = LOADING_MORE_KEY) { LoadingStateComponent() }
}

// True while the results are being asked for again, which is when the pull-to-refresh indicator spins.
private fun UiSongsContentState.Search.isRefreshing() =
    this is UiSongsContentState.SearchResults && status == UiSearchResultsState.Refreshing

@FullPreview
@Composable
private fun FirstLaunchWithSuggestionsPreview() =
    SongsListSectionPreview(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)))

@FullPreview
@Composable
private fun HistoryPreview() = SongsListSectionPreview(UiSongsContentState.History(SampleTracks.take(5)))

@FullPreview
@Composable
private fun SearchLoadingPreview() = SongsListSectionPreview(UiSongsContentState.SearchLoading)

@FullPreview
@Composable
private fun SearchErrorPreview() = SongsListSectionPreview(UiSongsContentState.SearchError(UiSongsError.Network))

@FullPreview
@Composable
private fun SearchNoResultsPreview() = SongsListSectionPreview(UiSongsContentState.SearchNoResults(searchText = "asfasgs"))

@FullPreview
@Composable
private fun SearchResultsPreview() =
    SongsListSectionPreview(UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore))

@FullPreview
@Composable
private fun SearchResultsLoadingMorePreview() =
    SongsListSectionPreview(UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.LoadingMore))

@FullPreview
@Composable
private fun SearchResultsWithFailedUpdatePreview() =
    SongsListSectionPreview(UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.Failed(UiSongsError.Network)))

@Composable
private fun SongsListSectionPreview(content: UiSongsContentState) = PreviewSurface {
    SongsListSection(content = content, callbacks = SongsCallbacks.None, listState = rememberLazyListState())
}
