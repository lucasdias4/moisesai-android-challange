package com.moisesai.challange.feature.songs.presentation.composable.screen.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NearestToMidlineTest {

    @Test
    fun `GIVEN three cards, WHEN the second is closest to the line, THEN its index is returned`() {
        // Given
        val centers = listOf(100f, 400f, 700f)

        // When
        val nearest = nearestToMidline(centers, midline = 416f)

        // Then
        assertEquals(1, nearest)
    }

    @Test
    fun `GIVEN two cards equally far from the line, WHEN asking for the nearest, THEN the first one wins`() {
        // Given
        val centers = listOf(300f, 500f)

        // When
        val nearest = nearestToMidline(centers, midline = 400f)

        // Then
        assertEquals(0, nearest)
    }

    @Test
    fun `GIVEN no cards, WHEN asking for the nearest, THEN there is none`() {
        // Given
        val centers = emptyList<Float>()

        // When
        val nearest = nearestToMidline(centers, midline = 416f)

        // Then
        assertNull(nearest)
    }
}
