package com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse

import org.junit.Assert.assertEquals
import org.junit.Test

class CollapseProgressTest {

    @Test
    fun `GIVEN a list that has not scrolled, WHEN asking for the progress, THEN the header is not collapsed`() {
        // Given / When
        val progress = collapseProgressAt(scrollOffsetPx = 0, collapseDistancePx = 200f)

        // Then
        assertEquals(0f, progress, 0f)
    }

    @Test
    fun `GIVEN a list scrolled half the distance, WHEN asking for the progress, THEN the header is half collapsed`() {
        // Given / When
        val progress = collapseProgressAt(scrollOffsetPx = 100, collapseDistancePx = 200f)

        // Then
        assertEquals(0.5f, progress, 0f)
    }

    @Test
    fun `GIVEN a list scrolled further than the distance, WHEN asking for the progress, THEN it stops at fully collapsed`() {
        // Given / When
        val progress = collapseProgressAt(scrollOffsetPx = 500, collapseDistancePx = 200f)

        // Then
        assertEquals(1f, progress, 0f)
    }
}
