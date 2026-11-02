package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.LoadingStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext

/** A search is running and nothing is known yet: bouncing bars and faint rows where the results will be. */
internal fun LazyListScope.showSongsListForSearchLoadingVariant(context: ListContext) {
    item(key = "loading") { LoadingStateComponent(context.swapModifier) }
}

@FullPreview
@Composable
private fun SearchLoadingPreview() = SongsListPreviewSurface { context -> showSongsListForSearchLoadingVariant(context) }
