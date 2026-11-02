package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.ArtworkCardComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.SectionLabelComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.CardSpacing
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.recentCardKey
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState

/** Something was played before: the recent songs as cards, the latest one marked as just played. */
internal fun LazyListScope.showSongsListForHistoryVariant(context: ListContext, history: UiSongsContentState.History) {
    item(key = "recents-label") {
        SectionLabelComponent(text = stringResource(R.string.songs_recently_played), modifier = context.swapModifier)
    }
    itemsIndexed(history.recents, key = { index, track -> recentCardKey(track, index) }) { index, track ->
        ArtworkCardComponent(
            track = track,
            badge = stringResource(if (index == 0) R.string.songs_badge_just_played else R.string.songs_badge_recent),
            onClick = { context.actions.onTrackClick(track.id) },
            reveal = UiReveal(index, context.tracker),
            modifier = context.swapModifier.padding(bottom = CardSpacing),
        )
    }
}

@FullPreview
@Composable
private fun HistoryPreview() = SongsListPreviewSurface { context ->
    showSongsListForHistoryVariant(context, UiSongsContentState.History(SampleTracks.take(3)))
}
