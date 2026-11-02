package com.moisesai.challange.feature.songs.presentation.composable.screen.helper

import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmbientArtUrlTest {

    private val first = aTrack(id = 1, artUrl = "small-1", largeArtUrl = "large-1")
    private val second = aTrack(id = 2, artUrl = "small-2", largeArtUrl = "large-2")

    @Test
    fun `GIVEN search results, WHEN choosing the cover, THEN it is the small cover of the first result`() {
        // Given
        val content = UiSongsContentState.SearchResults(listOf(first, second), UiSearchResultsState.HasMore)

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = 1)

        // Then
        assertEquals("small-1", artUrl)
    }

    @Test
    fun `GIVEN the history, WHEN the second card is the nearest, THEN the cover is the large cover of the second card`() {
        // Given
        val content = UiSongsContentState.History(listOf(first, second))

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = 1)

        // Then
        assertEquals("large-2", artUrl)
    }

    @Test
    fun `GIVEN the history, WHEN no card is on screen yet, THEN the cover is the large cover of the first card`() {
        // Given
        val content = UiSongsContentState.History(listOf(first, second))

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = null)

        // Then
        assertEquals("large-1", artUrl)
    }

    @Test
    fun `GIVEN the first launch with suggestions, WHEN the second card is the nearest, THEN the cover is the large cover of the second card`() {
        // Given
        val content = UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(listOf(first, second)))

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = 1)

        // Then
        assertEquals("large-2", artUrl)
    }

    @Test
    fun `GIVEN the first launch while suggestions load, WHEN choosing the cover, THEN there is none`() {
        // Given
        val content = UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading)

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = 0)

        // Then
        assertNull(artUrl)
    }

    @Test
    fun `GIVEN the first launch with no suggestions, WHEN choosing the cover, THEN there is none`() {
        // Given
        val content = UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable)

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = 0)

        // Then
        assertNull(artUrl)
    }

    @Test
    fun `GIVEN a search is loading, WHEN choosing the cover, THEN there is none`() {
        // Given
        val content = UiSongsContentState.SearchLoading

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = null)

        // Then
        assertNull(artUrl)
    }

    @Test
    fun `GIVEN a search failed, WHEN choosing the cover, THEN there is none`() {
        // Given
        val content = UiSongsContentState.SearchError(UiSongsError.Network)

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = null)

        // Then
        assertNull(artUrl)
    }

    @Test
    fun `GIVEN a search found nothing, WHEN choosing the cover, THEN there is none`() {
        // Given
        val content = UiSongsContentState.SearchNoResults("zzzz")

        // When
        val artUrl = content.ambientArtUrl(nearestCardIndex = null)

        // Then
        assertNull(artUrl)
    }

    private fun aTrack(id: Long, artUrl: String, largeArtUrl: String) = UiTrack(
        id = id,
        name = "Levitating",
        artistName = "Dua Lipa",
        artUrl = artUrl,
        largeArtUrl = largeArtUrl,
        durationText = "3:24",
    )
}
