package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.LoadingStateComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface

private const val LOADING_KEY = "loading"

/** A search is running and nothing is known yet: a loader where the songs will be. */
internal fun LazyListScope.showSongsListForSearchLoadingVariant() {
    item(key = LOADING_KEY) { LoadingStateComponent() }
}

@FullPreview
@Composable
private fun SearchLoadingPreview() = SongsListPreviewSurface { showSongsListForSearchLoadingVariant() }
