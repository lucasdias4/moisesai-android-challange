package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.NoResultsStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

/** A search worked and found nothing: says so and shows what was searched for. */
internal fun LazyListScope.showSongsListForSearchNoResultsVariant(
    context: ListContext,
    noResults: UiSongsContentState.SearchNoResults,
) {
    item(key = "no-results") { NoResultsStateComponent(noResults.searchText, context.swapModifier) }
}

@FullPreview
@Composable
private fun SearchNoResultsPreview() = SongsListPreviewSurface { context ->
    showSongsListForSearchNoResultsVariant(context, UiSongsContentState.SearchNoResults("asfasgs"))
}
