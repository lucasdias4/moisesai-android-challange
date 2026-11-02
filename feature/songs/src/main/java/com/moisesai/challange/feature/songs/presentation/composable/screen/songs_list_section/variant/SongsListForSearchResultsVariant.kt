package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.LoadingStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.PartialErrorMessageComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.showSongRows
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

private const val PARTIAL_ERROR_KEY = "partial-error"
private const val RESULT_KEY_PREFIX = "result"
private const val LOADING_MORE_KEY = "loading-more"

/**
 * A search found songs: one row for each, with a message above them when an update failed and a loader below them
 * while the next page arrives.
 */
internal fun LazyListScope.showSongsListForSearchResultsVariant(actions: SongsActions, results: UiSongsContentState.SearchResults) {
    if (results.status is UiSearchResultsState.Failed) item(key = PARTIAL_ERROR_KEY) { PartialErrorMessageComponent() }
    showSongRows(results.tracks, RESULT_KEY_PREFIX, actions.onTrackClick)
    if (results.status == UiSearchResultsState.LoadingMore) item(key = LOADING_MORE_KEY) { LoadingStateComponent() }
}

@FullPreview
@Composable
private fun SearchResultsWithMorePreview() = SongsListPreviewSurface {
    showSongsListForSearchResultsVariant(
        SongsActions.None,
        UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore),
    )
}

@FullPreview
@Composable
private fun SearchResultsLoadingMorePreview() = SongsListPreviewSurface {
    showSongsListForSearchResultsVariant(
        SongsActions.None,
        UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.LoadingMore),
    )
}

@FullPreview
@Composable
private fun SearchResultsWithFailedUpdatePreview() = SongsListPreviewSurface {
    val failedUpdate = UiSearchResultsState.Failed(UiSongsError.Network)
    showSongsListForSearchResultsVariant(SongsActions.None, UiSongsContentState.SearchResults(SampleTracks, failedUpdate))
}
