package com.moisesai.challange.feature.songs.data.repository

import java.util.Locale

private val WhitespaceRuns = Regex("\\s+")

/**
 * The key a search is saved under and the term sent to iTunes. "Daft  Punk " and "daft punk" are the
 * same search, so they share one saved result and one request (iTunes is not case sensitive).
 */
internal fun normalizeSearchText(searchText: String): String =
    searchText.trim().replace(WhitespaceRuns, " ").lowercase(Locale.ROOT)
