package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.variant

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.ArtworkCardComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.GhostHistoryCardComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.SectionLabelComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.SuggestionChipsComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.CardSpacing
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SongsListPreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.suggestedCardKey
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

// The chips already include 4 dp of touch area above the chips and below them, which the label below makes up for.
private val LabelTopPaddingAfterChips = 18.dp

/**
 * First launch: the chips to start a search, the suggested songs as cards, and a placeholder where the history will
 * be. The suggestions are looked up first, so while they load nothing below the chips is drawn, and the cards and
 * the placeholder then arrive together without anything jumping.
 */
internal fun LazyListScope.showSongsListForFirstLaunchVariant(context: ListContext, firstLaunch: UiSongsContentState.FirstLaunch) {
    item(key = "chips") {
        SuggestionChipsComponent(onSuggestionClick = context.actions.onSuggestionClick, modifier = context.swapModifier)
    }
    val suggestions = firstLaunch.suggestions
    if (suggestions is UiSuggestionsState.Loading) return
    var cardIndex = 0
    if (suggestions is UiSuggestionsState.Loaded) {
        item(key = "start-here-label") {
            SectionLabelComponent(
                text = stringResource(R.string.songs_start_here),
                topPadding = LabelTopPaddingAfterChips,
                modifier = context.swapModifier,
            )
        }
        itemsIndexed(suggestions.tracks, key = { index, track -> suggestedCardKey(track, index) }) { index, track ->
            ArtworkCardComponent(
                track = track,
                badge = stringResource(R.string.songs_badge_suggested),
                onClick = { context.actions.onTrackClick(track.id) },
                reveal = UiReveal(index, context.tracker),
                modifier = context.swapModifier.padding(bottom = CardSpacing),
            )
        }
        cardIndex = suggestions.tracks.size
    }
    item(key = "ghost") {
        GhostHistoryCardComponent(
            reveal = UiReveal(cardIndex, context.tracker),
            modifier = context.swapModifier.padding(top = if (cardIndex == 0) CardSpacing else 0.dp, bottom = CardSpacing),
        )
    }
}

@FullPreview
@Composable
private fun FirstLaunchWithSuggestionsPreview() = SongsListPreviewSurface { context ->
    showSongsListForFirstLaunchVariant(context, UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)))
}

@FullPreview
@Composable
private fun FirstLaunchWhileLookingUpSuggestionsPreview() = SongsListPreviewSurface { context ->
    showSongsListForFirstLaunchVariant(context, UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading))
}

@FullPreview
@Composable
private fun FirstLaunchWithoutSuggestionsPreview() = SongsListPreviewSurface { context ->
    showSongsListForFirstLaunchVariant(context, UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable))
}
