package com.moisesai.challange.feature.songs.presentation.composable.animation.reveal

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RevealTrackerTest {

    @Test
    fun `GIVEN a new tracker, WHEN asking if an item has arrived, THEN it has not`() {
        // Given
        val tracker = RevealTracker()

        // When
        val hasArrived = tracker.hasRevealed("card-1-0")

        // Then
        assertFalse(hasArrived)
    }

    @Test
    fun `GIVEN an item was marked as arrived, WHEN asking if it has arrived, THEN it has`() {
        // Given
        val tracker = RevealTracker()
        tracker.markRevealed("card-1-0")

        // When
        val hasArrived = tracker.hasRevealed("card-1-0")

        // Then
        assertTrue(hasArrived)
    }

    @Test
    fun `GIVEN an item was marked as arrived, WHEN asking about another item, THEN it has not arrived`() {
        // Given
        val tracker = RevealTracker()
        tracker.markRevealed("card-1-0")

        // When
        val hasArrived = tracker.hasRevealed("card-1-1")

        // Then
        assertFalse(hasArrived)
    }
}
