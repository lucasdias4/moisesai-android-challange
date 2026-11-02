package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private const val SUGGESTED_CARD_KEY_PREFIX = "suggested-"
private const val RECENT_CARD_KEY_PREFIX = "recent-"
private const val KEY_SEPARATOR = '-'

// Cards are keyed by position too, so a track that appears twice in one list cannot crash it.
internal fun suggestedCardKey(track: UiTrack, position: Int) = "$SUGGESTED_CARD_KEY_PREFIX${track.id}$KEY_SEPARATOR$position"

internal fun recentCardKey(track: UiTrack, position: Int) = "$RECENT_CARD_KEY_PREFIX${track.id}$KEY_SEPARATOR$position"

internal fun isCardKey(key: Any): Boolean =
    key is String && (key.startsWith(SUGGESTED_CARD_KEY_PREFIX) || key.startsWith(RECENT_CARD_KEY_PREFIX))

/** The position a card was keyed with, which is its index among the cards. */
internal fun cardPositionOf(cardKey: String): Int? = cardKey.substringAfterLast(KEY_SEPARATOR).toIntOrNull()
