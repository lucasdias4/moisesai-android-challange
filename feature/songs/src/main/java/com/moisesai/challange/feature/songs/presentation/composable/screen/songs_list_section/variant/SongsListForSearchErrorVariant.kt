package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.ErrorStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

private const val ERROR_KEY = "error"

/** A search failed and there is nothing else to show: what went wrong and a way to try again. */
internal fun LazyListScope.showSongsListForSearchErrorVariant(actions: SongsActions, searchError: UiSongsContentState.SearchError) {
    item(key = ERROR_KEY) { ErrorStateComponent(error = searchError.error, onRetry = actions.onRetry) }
}

@FullPreview
@Composable
private fun SearchErrorNetworkPreview() = SongsListPreviewSurface {
    showSongsListForSearchErrorVariant(SongsActions.None, UiSongsContentState.SearchError(UiSongsError.Network))
}

@FullPreview
@Composable
private fun SearchErrorUnknownPreview() = SongsListPreviewSurface {
    showSongsListForSearchErrorVariant(SongsActions.None, UiSongsContentState.SearchError(UiSongsError.Unknown))
}
