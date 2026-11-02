package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.NoResultsStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

private const val NO_RESULTS_KEY = "no-results"

/** A search worked and found nothing: says so and shows what was searched for. */
internal fun LazyListScope.showSongsListForSearchNoResultsVariant(noResults: UiSongsContentState.SearchNoResults) {
    item(key = NO_RESULTS_KEY) { NoResultsStateComponent(searchText = noResults.searchText) }
}

@FullPreview
@Composable
private fun SearchNoResultsPreview() = SongsListPreviewSurface {
    showSongsListForSearchNoResultsVariant(UiSongsContentState.SearchNoResults(searchText = "asfasgs"))
}
