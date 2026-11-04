package com.moisesai.challange.feature.songs.presentation.composable.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.composable.screen.header_section.HeaderSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.helper.LoadMoreEffect
import com.moisesai.challange.feature.songs.presentation.composable.screen.search_field_section.SearchFieldSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.song_options_section.SongOptionsSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.SongsListSection
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrackOptionsState

private const val FIRST_ITEM = 0
private const val EMPTY_QUERY = ""

/**
 * The songs screen: its name, a search field and the list of songs, and the sheet with the choices for a song. It draws [uiState] and reports what the user does
 * to [callbacks].
 */
@Composable
internal fun SongsScreen(uiState: UiSongsUiState, callbacks: SongsCallbacks, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    ScrollToTopEffect(contentKind = uiState.content::class, listState = listState)
    LoadMoreEffect(listState = listState, onLoadMore = callbacks.onLoadMore)
    ClearQueryOnBackEffect(query = uiState.query, onQueryChange = callbacks.onQueryChange)

    // The magnifier brings the field back and the user came to type, so the field takes the focus when it returns.
    val searchFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchFieldFocusRequested by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isSearchFieldFocusRequested, uiState.isSearchFieldVisible) {
        if (isSearchFieldFocusRequested && uiState.isSearchFieldVisible) {
            searchFieldFocusRequester.requestFocus()
            keyboardController?.show()
            isSearchFieldFocusRequested = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.design.colors.background00)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                title = stringResource(R.string.songs_title),
                onSearchClick = if (uiState.isSearchFieldVisible) {
                    null
                } else {
                    {
                        isSearchFieldFocusRequested = true
                        callbacks.onSearchIconClick()
                    }
                },
            )
            if (uiState.isSearchFieldVisible) {
                SearchFieldSection(query = uiState.query, callbacks = callbacks, modifier = Modifier.focusRequester(searchFieldFocusRequester))
            }
            SongsListSection(
                content = uiState.content,
                callbacks = callbacks,
                listState = listState,
                modifier = Modifier.weight(1f),
            )
        }
        SongOptionsSection(trackOptions = uiState.trackOptions, callbacks = callbacks)
    }
}

// A different kind of content (the loader, then the results) starts at the top; more results of the same kind do not.
@Composable
private fun ScrollToTopEffect(contentKind: Any, listState: LazyListState) {
    LaunchedEffect(contentKind) { listState.scrollToItem(FIRST_ITEM) }
}

// Back leaves a search: the query is cleared and the field gives up focus.
@Composable
private fun ClearQueryOnBackEffect(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    BackHandler(enabled = query.isNotEmpty()) {
        onQueryChange(EMPTY_QUERY)
        focusManager.clearFocus()
    }
}

@Composable
private fun SongsScreenPreviewOf(content: UiSongsContentState, query: String = "", isSearchFieldVisible: Boolean = true) {
    MoisesaiChallangeTheme {
        SongsScreen(uiState = UiSongsUiState(query = query, isSearchFieldVisible = isSearchFieldVisible, content = content, trackOptions = UiTrackOptionsState.Hidden), callbacks = SongsCallbacks.None)
    }
}

@FullPreview
@Composable
private fun SongsScreenFirstLaunchPreview() =
    SongsScreenPreviewOf(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)))

@FullPreview
@Composable
private fun SongsScreenHistoryPreview() = SongsScreenPreviewOf(UiSongsContentState.History(SampleTracks))

@FullPreview
@Composable
private fun SongsScreenResultsPreview() = SongsScreenPreviewOf(
    content = UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore),
    query = "levitating",
)

@FullPreview
@Composable
private fun SongsScreenSearchFieldTuckedAwayPreview() = SongsScreenPreviewOf(
    content = UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore),
    query = "levitating",
    isSearchFieldVisible = false,
)

@FullPreview
@Composable
private fun SongsScreenLoadingPreview() = SongsScreenPreviewOf(UiSongsContentState.SearchLoading, query = "levitating")

@FullPreview
@Composable
private fun SongsScreenErrorPreview() =
    SongsScreenPreviewOf(UiSongsContentState.SearchError(UiSongsError.Network), query = "levitating")

@FullPreview
@Composable
private fun SongsScreenNoResultsPreview() =
    SongsScreenPreviewOf(UiSongsContentState.SearchNoResults(searchText = "zzzz"), query = "zzzz")
