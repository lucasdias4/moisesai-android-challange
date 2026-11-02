package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForFirstLaunchVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForHistoryVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchErrorVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchLoadingVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchNoResultsVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchResultsVariant
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

/** The one place that says which composable draws which state of the screen. */
internal fun LazyListScope.showSongsListVariantByState(context: ListContext, content: UiSongsContentState) {
    when (content) {
        is UiSongsContentState.FirstLaunch -> showSongsListForFirstLaunchVariant(context, content)
        is UiSongsContentState.History -> showSongsListForHistoryVariant(context, content)
        UiSongsContentState.SearchLoading -> showSongsListForSearchLoadingVariant(context)
        is UiSongsContentState.SearchError -> showSongsListForSearchErrorVariant(context, content)
        is UiSongsContentState.SearchNoResults -> showSongsListForSearchNoResultsVariant(context, content)
        is UiSongsContentState.SearchResults -> showSongsListForSearchResultsVariant(context, content)
    }
}
