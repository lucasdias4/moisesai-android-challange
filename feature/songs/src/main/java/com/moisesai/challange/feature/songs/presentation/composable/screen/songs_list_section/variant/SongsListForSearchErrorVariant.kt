package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.ErrorStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

/** A search failed and there is nothing else to show: what went wrong and a way to try again. */
internal fun LazyListScope.showSongsListForSearchErrorVariant(context: ListContext, searchError: UiSongsContentState.SearchError) {
    item(key = "error") { ErrorStateComponent(searchError.error, context.actions.onRetry, context.swapModifier) }
}

@FullPreview
@Composable
private fun SearchErrorNetworkPreview() = SongsListPreviewSurface { context ->
    showSongsListForSearchErrorVariant(context, UiSongsContentState.SearchError(UiSongsError.Network))
}

@FullPreview
@Composable
private fun SearchErrorUnknownPreview() = SongsListPreviewSurface { context ->
    showSongsListForSearchErrorVariant(context, UiSongsContentState.SearchError(UiSongsError.Unknown))
}
