package com.moisesai.challange.core.design.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistNameExtensionTest {

    @Test
    fun `GIVEN several artists, WHEN asking for the main artist, THEN it is the first one`() {
        // Given
        val artistName = "Justice, Tame Impala, SZA"

        // When
        val mainArtist = artistName.mainArtist()

        // Then
        assertEquals("Justice", mainArtist)
    }

    @Test
    fun `GIVEN one artist, WHEN asking for the main artist, THEN it is the same artist`() {
        // Given
        val artistName = "Dua Lipa"

        // When
        val mainArtist = artistName.mainArtist()

        // Then
        assertEquals("Dua Lipa", mainArtist)
    }
}
