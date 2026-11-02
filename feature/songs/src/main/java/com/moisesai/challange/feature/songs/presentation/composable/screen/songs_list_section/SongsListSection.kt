package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.BottomSpacing
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ScreenHorizontalPadding
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.SearchFieldHeight
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.previewListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.ScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.model.rememberScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.screen.big_header_section.BigHeaderSection
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.isRefreshing
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.showSongsListVariantByState
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

private const val HEADER_KEY = "header"
internal const val FIELD_SLOT_KEY = "field-slot"
private const val BOTTOM_SPACE_KEY = "bottom-space"
// Pulling down refreshes a search, so it only exists while one is shown; the home has nothing to refresh.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsListSection(content: UiSongsContentState, context: ListContext, scroll: ScrollContext) {
    if (content is UiSongsContentState.Search) {
        PullToRefreshBox(isRefreshing = content.isRefreshing(), onRefresh = context.actions.onRefresh) {
            SongsColumn(content, context, scroll)
        }
    } else {
        SongsColumn(content, context, scroll)
    }
}

@Composable
private fun SongsColumn(content: UiSongsContentState, context: ListContext, scroll: ScrollContext) {
    LazyColumn(
        state = scroll.listState,
        modifier = Modifier.fillMaxSize().imePadding(),
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
    ) {
        item(key = HEADER_KEY) { BigHeaderSection(scroll.collapseProgress) }
        // The real field floats above the list; this empty slot keeps its place in the list.
        item(key = FIELD_SLOT_KEY) { Spacer(Modifier.height(SearchFieldHeight)) }
        showSongsListVariantByState(context, content)
        item(key = BOTTOM_SPACE_KEY) { Spacer(Modifier.height(bottomSpacing())) }
    }
}

@Composable
private fun bottomSpacing(): Dp = BottomSpacing + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

@FullPreview
@Composable
private fun SongsListSectionFirstLaunchPreview() = PreviewSurface {
    SongsListSection(
        content = UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)),
        context = previewListContext(),
        scroll = rememberScrollContext(),
    )
}

@FullPreview
@Composable
private fun SongsListSectionResultsPreview() = PreviewSurface {
    SongsListSection(
        content = UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore),
        context = previewListContext(),
        scroll = rememberScrollContext(),
    )
}

@FullPreview
@Composable
private fun SongsListSectionFailedSearchPreview() = PreviewSurface {
    SongsListSection(
        content = UiSongsContentState.SearchError(UiSongsError.Network),
        context = previewListContext(),
        scroll = rememberScrollContext(),
    )
}
