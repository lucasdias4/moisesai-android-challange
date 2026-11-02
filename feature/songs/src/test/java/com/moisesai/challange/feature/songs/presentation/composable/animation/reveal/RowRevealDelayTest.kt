package com.moisesai.challange.feature.songs.presentation.composable.animation.reveal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RowRevealDelayTest {

    @Test
    fun `GIVEN the first row, WHEN asking how long it waits, THEN it does not wait`() {
        // Given / When
        val delayMillis = rowRevealDelayMillis(rowIndex = 0)

        // Then
        assertEquals(0L, delayMillis)
    }

    @Test
    fun `GIVEN the tenth row, WHEN asking how long it waits, THEN it waits nine staggers`() {
        // Given / When
        val delayMillis = rowRevealDelayMillis(rowIndex = 9)

        // Then
        assertEquals(810L, delayMillis)
    }

    @Test
    fun `GIVEN a row after the tenth, WHEN asking how long it waits, THEN it is not animated`() {
        // Given / When
        val delayMillis = rowRevealDelayMillis(rowIndex = 10)

        // Then
        assertNull(delayMillis)
    }
}
