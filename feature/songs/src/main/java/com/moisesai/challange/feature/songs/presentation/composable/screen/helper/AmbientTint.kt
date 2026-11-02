package com.moisesai.challange.feature.songs.presentation.composable.screen.helper

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil3.SingletonImageLoader
import com.moisesai.challange.core.design.ambient.ambientTintOf
import com.moisesai.challange.core.design.ambient.loadArtworkTint
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.cardPositionOf
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension.isCardKey
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import kotlin.math.abs

// The background is lit by the card that is nearest to this share of the height of the screen.
private const val AMBIENT_MIDLINE = 0.52f

private const val FIRST_CARD = 0

// The light the background has before any artwork has been read: the colour of the first suggested cover ("As").
private val AmbientDefaultTint = ambientTintOf(Color(210, 115, 86))

// The colour that lights the background: the first result while searching, otherwise the card nearest to the
// middle of the screen, so the light changes as the cards scroll past it.
@Composable
internal fun rememberAmbientTint(content: UiSongsContentState, listState: LazyListState): Color {
    val nearestCardIndex by remember(listState) { derivedStateOf { listState.nearestCardIndex() } }
    return rememberArtworkTint(content.ambientArtUrl(nearestCardIndex))
}

/** The cover that lights the background, or null when nothing on screen has one. */
internal fun UiSongsContentState.ambientArtUrl(nearestCardIndex: Int?): String? {
    val cardIndex = nearestCardIndex ?: FIRST_CARD
    return when (this) {
        is UiSongsContentState.SearchResults -> tracks.firstOrNull()?.artUrl
        is UiSongsContentState.History -> recents.getOrNull(cardIndex)?.largeArtUrl
        is UiSongsContentState.FirstLaunch ->
            (suggestions as? UiSuggestionsState.Loaded)?.tracks?.getOrNull(cardIndex)?.largeArtUrl
        is UiSongsContentState.Search -> null
    }
}

// Keeps the last tint while the next cover loads, and remembers the ones already read.
@Composable
private fun rememberArtworkTint(artUrl: String?): Color {
    val context = LocalContext.current
    var tint by remember { mutableStateOf(AmbientDefaultTint) }
    val tintsByArtUrl = remember { HashMap<String, Color>() }
    LaunchedEffect(artUrl) {
        if (artUrl == null) return@LaunchedEffect
        val artworkTint = tintsByArtUrl[artUrl]
            ?: loadArtworkTint(context, SingletonImageLoader.get(context), artUrl)
            ?: return@LaunchedEffect
        tintsByArtUrl[artUrl] = artworkTint
        tint = artworkTint
    }
    return tint
}

private fun LazyListState.nearestCardIndex(): Int? {
    val cards = layoutInfo.visibleItemsInfo.filter { isCardKey(it.key) }
    val midline = layoutInfo.viewportSize.height * AMBIENT_MIDLINE
    val nearestCard = nearestToMidline(cards.map { it.centerY() }, midline) ?: return null
    return cardPositionOf(cards[nearestCard].key as String)
}

private fun LazyListItemInfo.centerY() = offset + size / 2f

/**
 * The index of the centre that lies closest to [midline], or null when there are none. The background takes its
 * colour from the card nearest to this line, so it changes as the cards scroll past it.
 */
internal fun nearestToMidline(centers: List<Float>, midline: Float): Int? =
    centers.indices.minByOrNull { abs(centers[it] - midline) }
