package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ListItemKeysTest {

    private val levitating = UiTrack(
        id = 1538003843,
        name = "Levitating",
        artistName = "Dua Lipa",
        artUrl = null,
        largeArtUrl = null,
        durationText = "3:24",
    )

    @Test
    fun `GIVEN a suggested card at a position, WHEN its position is read from its key, THEN it is the same position`() {
        // Given
        val key = suggestedCardKey(levitating, position = 4)

        // When
        val position = cardPositionOf(key)

        // Then
        assertEquals(4, position)
    }

    @Test
    fun `GIVEN a recent card at a position, WHEN its position is read from its key, THEN it is the same position`() {
        // Given
        val key = recentCardKey(levitating, position = 2)

        // When
        val position = cardPositionOf(key)

        // Then
        assertEquals(2, position)
    }

    @Test
    fun `GIVEN the same track in two positions, WHEN the keys are built, THEN they differ`() {
        // Given / When
        val first = recentCardKey(levitating, position = 0)
        val second = recentCardKey(levitating, position = 1)

        // Then
        assertTrue(first != second)
    }

    @Test
    fun `GIVEN the key of a suggested card, WHEN asking if it is a card, THEN it is`() {
        // Given
        val key = suggestedCardKey(levitating, position = 0)

        // When
        val isCard = isCardKey(key)

        // Then
        assertTrue(isCard)
    }

    @Test
    fun `GIVEN the key of a recent card, WHEN asking if it is a card, THEN it is`() {
        // Given
        val key = recentCardKey(levitating, position = 0)

        // When
        val isCard = isCardKey(key)

        // Then
        assertTrue(isCard)
    }

    @Test
    fun `GIVEN the key of a result row, WHEN asking if it is a card, THEN it is not`() {
        // Given
        val key = "result-1538003843-0"

        // When
        val isCard = isCardKey(key)

        // Then
        assertFalse(isCard)
    }

    @Test
    fun `GIVEN a key that is not text, WHEN asking if it is a card, THEN it is not`() {
        // Given
        val key = 7

        // When
        val isCard = isCardKey(key)

        // Then
        assertFalse(isCard)
    }

    @Test
    fun `GIVEN a key with no position, WHEN its position is read, THEN there is none`() {
        // Given
        val key = "recent-header"

        // When
        val position = cardPositionOf(key)

        // Then
        assertNull(position)
    }
}
