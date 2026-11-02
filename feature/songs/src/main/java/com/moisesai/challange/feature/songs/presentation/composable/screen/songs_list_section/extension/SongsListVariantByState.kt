package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import androidx.compose.foundation.lazy.LazyListScope
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForFirstLaunchVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForHistoryVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchErrorVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchLoadingVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchNoResultsVariant
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant.showSongsListForSearchResultsVariant
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

/** The one place that says which variant of the list draws which state of the screen. */
internal fun LazyListScope.showSongsListVariantByState(actions: SongsActions, content: UiSongsContentState) {
    when (content) {
        is UiSongsContentState.FirstLaunch -> showSongsListForFirstLaunchVariant(actions, content)
        is UiSongsContentState.History -> showSongsListForHistoryVariant(actions, content)
        UiSongsContentState.SearchLoading -> showSongsListForSearchLoadingVariant()
        is UiSongsContentState.SearchError -> showSongsListForSearchErrorVariant(actions, content)
        is UiSongsContentState.SearchNoResults -> showSongsListForSearchNoResultsVariant(content)
        is UiSongsContentState.SearchResults -> showSongsListForSearchResultsVariant(actions, content)
    }
}
