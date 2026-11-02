package com.moisesai.challange.feature.songs.presentation.composable.screen

import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ContentKeyTest {

    private val levitating = UiTrack(
        id = 1,
        name = "Levitating",
        artistName = "Dua Lipa",
        artUrl = null,
        largeArtUrl = null,
        durationText = "3:24",
    )

    @Test
    fun `GIVEN the first launch and the history, WHEN comparing their keys, THEN they differ`() {
        // Given
        val firstLaunch = stateOf(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading))
        val history = stateOf(UiSongsContentState.History(listOf(levitating)))

        // When
        val keys = firstLaunch.contentKey() to history.contentKey()

        // Then
        assertNotEquals(keys.first, keys.second)
    }

    @Test
    fun `GIVEN the home and a search, WHEN comparing their keys, THEN they differ`() {
        // Given
        val history = stateOf(UiSongsContentState.History(listOf(levitating)))
        val search = stateOf(UiSongsContentState.SearchLoading)

        // When
        val keys = history.contentKey() to search.contentKey()

        // Then
        assertNotEquals(keys.first, keys.second)
    }

    @Test
    fun `GIVEN a search that is loading and one with results, WHEN comparing their keys, THEN they are the same`() {
        // Given
        val loading = stateOf(UiSongsContentState.SearchLoading)
        val results = stateOf(UiSongsContentState.SearchResults(listOf(levitating), UiSearchResultsState.HasMore))

        // When
        val keys = loading.contentKey() to results.contentKey()

        // Then
        assertEquals(keys.first, keys.second)
    }

    @Test
    fun `GIVEN a search that failed and one that found nothing, WHEN comparing their keys, THEN they are the same`() {
        // Given
        val error = stateOf(UiSongsContentState.SearchError(UiSongsError.Network))
        val noResults = stateOf(UiSongsContentState.SearchNoResults("zzzz"))

        // When
        val keys = error.contentKey() to noResults.contentKey()

        // Then
        assertEquals(keys.first, keys.second)
    }

    @Test
    fun `GIVEN results that are loading more, WHEN comparing with results that are idle, THEN the keys are the same`() {
        // Given
        val loadingMore = stateOf(UiSongsContentState.SearchResults(listOf(levitating), UiSearchResultsState.LoadingMore))
        val idle = stateOf(UiSongsContentState.SearchResults(listOf(levitating), UiSearchResultsState.Complete))

        // When
        val keys = loadingMore.contentKey() to idle.contentKey()

        // Then
        assertEquals(keys.first, keys.second)
    }

    private fun stateOf(content: UiSongsContentState) = UiSongsUiState(content = content)
}
