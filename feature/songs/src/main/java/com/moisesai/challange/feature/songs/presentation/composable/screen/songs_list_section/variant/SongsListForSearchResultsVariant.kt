package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.IdleEqualizerAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.PartialErrorMessageComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.SectionLabelComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.TrackRowComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

private val RowSpacing = 4.dp
private val LoadMoreSpacing = 14.dp

/**
 * A search found songs: one row for each, with a message above them when an update failed and a loader below them
 * while the next page arrives.
 */
internal fun LazyListScope.showSongsListForSearchResultsVariant(context: ListContext, results: UiSongsContentState.SearchResults) {
    if (results.status is UiSearchResultsState.Failed) item(key = "partial-error") { PartialErrorMessageComponent(context.swapModifier) }
    item(key = "results-label") {
        SectionLabelComponent(text = stringResource(R.string.songs_results_title), modifier = context.swapModifier)
    }
    // Rows are keyed by position too, so a track that appears twice in one list cannot crash it.
    itemsIndexed(results.tracks, key = { index, track -> "result-${track.id}-$index" }) { index, track ->
        TrackRowComponent(
            track = track,
            reveal = UiReveal(index, context.tracker),
            onClick = { context.actions.onTrackClick(track.id) },
            modifier = context.swapModifier.padding(bottom = RowSpacing),
        )
    }
    if (results.status == UiSearchResultsState.LoadingMore) {
        item(key = "loading-more") {
            CenteredInRow { IdleEqualizerAnimation(Modifier.padding(vertical = LoadMoreSpacing)) }
        }
    }
}

// Centres a small item in the width of the list.
@Composable
private fun CenteredInRow(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
}

@FullPreview
@Composable
private fun SearchResultsWithMorePreview() = SongsListPreviewSurface { context ->
    showSongsListForSearchResultsVariant(context, UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.HasMore))
}

@FullPreview
@Composable
private fun SearchResultsLoadingMorePreview() = SongsListPreviewSurface { context ->
    showSongsListForSearchResultsVariant(context, UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.LoadingMore))
}

@FullPreview
@Composable
private fun SearchResultsWithFailedUpdatePreview() = SongsListPreviewSurface { context ->
    val failed = UiSearchResultsState.Failed(UiSongsError.Network)
    showSongsListForSearchResultsVariant(context, UiSongsContentState.SearchResults(SampleTracks, failed))
}
