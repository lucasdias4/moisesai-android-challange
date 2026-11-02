package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.showSongRows
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

private const val SUGGESTED_KEY_PREFIX = "suggested"

/** Nothing was played yet: the songs we suggest, once they are found. While they load, or if there are none, nothing is drawn. */
internal fun LazyListScope.showSongsListForFirstLaunchVariant(actions: SongsActions, firstLaunch: UiSongsContentState.FirstLaunch) {
    val suggestions = firstLaunch.suggestions as? UiSuggestionsState.Loaded ?: return
    showSongRows(suggestions.tracks, SUGGESTED_KEY_PREFIX, actions.onTrackClick)
}

@FullPreview
@Composable
private fun FirstLaunchWithSuggestionsPreview() = SongsListPreviewSurface {
    showSongsListForFirstLaunchVariant(
        SongsActions.None,
        UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)),
    )
}
