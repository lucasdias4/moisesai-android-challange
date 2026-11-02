package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.showSongRows
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

private const val RECENT_KEY_PREFIX = "recent"

/** Something was played before: the recent songs, the latest first. */
internal fun LazyListScope.showSongsListForHistoryVariant(actions: SongsActions, history: UiSongsContentState.History) {
    showSongRows(history.recents, RECENT_KEY_PREFIX, actions.onTrackClick)
}

@FullPreview
@Composable
private fun HistoryPreview() = SongsListPreviewSurface {
    showSongsListForHistoryVariant(SongsActions.None, UiSongsContentState.History(SampleTracks.take(5)))
}
