package com.moisesai.challange.feature.songs.data.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionTest {

    @Test
    fun `GIVEN a search text with capitals, WHEN normalised, THEN it is lowercase`() {
        // Given
        val searchText = "Daft Punk"

        // When
        val normalized = searchText.normalizeSearchText()

        // Then
        assertEquals("daft punk", normalized)
    }

    @Test
    fun `GIVEN a search text with spaces around it, WHEN normalised, THEN the spaces are trimmed`() {
        // Given
        val searchText = "  levitating  "

        // When
        val normalized = searchText.normalizeSearchText()

        // Then
        assertEquals("levitating", normalized)
    }

    @Test
    fun `GIVEN a search text with repeated spaces inside, WHEN normalised, THEN each run becomes one space`() {
        // Given
        val searchText = "kendrick \t  lamar"

        // When
        val normalized = searchText.normalizeSearchText()

        // Then
        assertEquals("kendrick lamar", normalized)
    }
}
