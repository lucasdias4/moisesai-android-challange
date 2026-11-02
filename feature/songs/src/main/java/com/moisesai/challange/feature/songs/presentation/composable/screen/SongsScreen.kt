package com.moisesai.challange.feature.songs.presentation.composable.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.moisesai.challange.core.design.ambient.AmbientBackground
import com.moisesai.challange.core.design.components.GrainOverlay
import com.moisesai.challange.core.design.components.glassSource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.feature.songs.presentation.composable.animation.content_swap.contentSwapAlpha
import com.moisesai.challange.feature.songs.presentation.composable.animation.content_swap.rememberContentSwap
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.rememberRevealTracker
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.model.rememberScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.screen.compact_header_section.CompactHeaderSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.helper.LoadMoreEffect
import com.moisesai.challange.feature.songs.presentation.composable.screen.helper.rememberAmbientTint
import com.moisesai.challange.feature.songs.presentation.composable.screen.search_field_section.SearchFieldSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.SongsListSection
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

private const val FIRST_ITEM = 0
private const val EMPTY_QUERY = ""
private const val SEARCH_TEXT = "levitating"

/**
 * The home screen: the app name, a search field, and either the first-launch invitation, the history, or the results
 * of a search, all on a background that takes its colour from the artwork in view. It draws [uiState] and reports
 * what the user does to [actions].
 */
@Composable
internal fun SongsScreen(
    uiState: UiSongsUiState,
    actions: SongsActions,
    modifier: Modifier = Modifier,
) {
    val contentSwap = rememberContentSwap(uiState, UiSongsUiState::contentKey)
    val shownState = contentSwap.displayed
    val scroll = rememberScrollContext()
    val revealTracker = rememberRevealTracker(shownState.contentKey())

    ScrollToTopEffect(shownState.contentKey(), scroll.listState)
    LoadMoreEffect(scroll.listState, actions.onLoadMore)
    ClearQueryOnBackEffect(uiState.query, actions.onQueryChange)

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxSize().glassSource(scroll.glassState)) {
            AmbientBackground(tint = rememberAmbientTint(shownState.content, scroll.listState))
            SongsListSection(
                content = shownState.content,
                context = ListContext(actions, Modifier.contentSwapAlpha(contentSwap), revealTracker),
                scroll = scroll,
            )
        }
        SearchFieldSection(uiState, actions, scroll)
        CompactHeaderSection(scroll)
        GrainOverlay()
    }
}

// What the list shows depends on this alone, so a change of it is a change of content and fades; a change of
// anything else (results arriving, a refresh) is the same content updating.
internal fun UiSongsUiState.contentKey(): Any = when (content) {
    is UiSongsContentState.Home -> content::class
    is UiSongsContentState.Search -> UiSongsContentState.Search::class
}

// A different kind of content starts at the top.
@Composable
private fun ScrollToTopEffect(contentKey: Any, listState: LazyListState) {
    LaunchedEffect(contentKey) { listState.scrollToItem(FIRST_ITEM) }
}

// Back leaves a search for the first-launch home: the query is cleared and the field gives up focus, so it shows its
// resting look with the placeholder.
@Composable
private fun ClearQueryOnBackEffect(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    BackHandler(enabled = query.isNotEmpty()) {
        onQueryChange(EMPTY_QUERY)
        focusManager.clearFocus()
    }
}

@Composable
private fun PreviewOf(content: UiSongsContentState, query: String = SEARCH_TEXT) {
    MoisesaiChallangeTheme {
        SongsScreen(uiState = UiSongsUiState(query = query, content = content), actions = SongsActions.None)
    }
}

private fun resultsWith(status: UiSearchResultsState) = UiSongsContentState.SearchResults(SampleTracks, status)

@FullPreview
@Composable
private fun SongsScreenEmptyPreview() =
    PreviewOf(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)), query = "")

@FullPreview
@Composable
private fun SongsScreenEmptyOfflinePreview() =
    PreviewOf(UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable), query = "")

@FullPreview
@Composable
private fun SongsScreenHistoryPreview() = PreviewOf(UiSongsContentState.History(SampleTracks.take(3)), query = "")

@FullPreview
@Composable
private fun SongsScreenResultsPreview() = PreviewOf(resultsWith(UiSearchResultsState.HasMore))

@FullPreview
@Composable
private fun SongsScreenLoadingMorePreview() = PreviewOf(resultsWith(UiSearchResultsState.LoadingMore))

@FullPreview
@Composable
private fun SongsScreenLoadingPreview() = PreviewOf(UiSongsContentState.SearchLoading)

@FullPreview
@Composable
private fun SongsScreenNetworkErrorPreview() = PreviewOf(UiSongsContentState.SearchError(UiSongsError.Network))

@FullPreview
@Composable
private fun SongsScreenNoResultsPreview() = PreviewOf(UiSongsContentState.SearchNoResults("zzzz"), query = "zzzz")

@FullPreview
@Composable
private fun SongsScreenPartialErrorPreview() =
    PreviewOf(resultsWith(UiSearchResultsState.Failed(UiSongsError.Network)))
