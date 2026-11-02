package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
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
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.isRefreshing
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.showSongsListVariantByState
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

private val ListStartPadding = 24.dp
private val ListEndPadding = 16.dp
private val ListTopPadding = 8.dp

// The design leaves this much room under the last song, for the mini player that will float above the list.
private val ListBottomPadding = 240.dp

// The songs fade out under the search field: black at 80% where they leave, clear 16 further down.
private val TopShadowHeight = 16.dp
private const val TOP_SHADOW_START_ALPHA = 0.8f

/**
 * The songs of the screen, drawn by the variant that fits [content]. While a search is shown, pulling the list down
 * asks for the search again.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsListSection(
    content: UiSongsContentState,
    actions: SongsActions,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (content is UiSongsContentState.Search) {
            PullToRefreshBox(isRefreshing = content.isRefreshing(), onRefresh = actions.onRefresh) {
                SongsColumn(content, actions, listState)
            }
        } else {
            SongsColumn(content, actions, listState)
        }
        TopShadow(modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun SongsColumn(content: UiSongsContentState, actions: SongsActions, listState: LazyListState) {
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
        showSongsListVariantByState(actions, content)
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

@FullPreview
@Composable
private fun SongsListSectionPreview() = PreviewSurface {
    SongsListSection(
        content = UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore),
        actions = SongsActions.None,
        listState = rememberLazyListState(),
    )
}
