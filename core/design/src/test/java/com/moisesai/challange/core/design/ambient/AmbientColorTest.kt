package com.moisesai.challange.core.design.ambient

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class AmbientColorTest {

    @Test
    fun `GIVEN a dark cover, WHEN its tint is computed, THEN it is brightened by at most two point six`() {
        // Given: the average colour of the Get Lucky cover
        val average = Color(48, 49, 52)

        // When
        val tint = ambientTintOf(average)

        // Then: 215 / 52 is above the limit, so every channel is multiplied by 2.6
        assertEquals(Color(125, 127, 135), tint)
    }

    @Test
    fun `GIVEN a bright cover, WHEN its tint is computed, THEN its strongest channel is scaled to 215`() {
        // Given: the average colour of the ocean eyes cover
        val average = Color(225, 166, 107)

        // When
        val tint = ambientTintOf(average)

        // Then: 215 / 225 is below one, so the colour is slightly dimmed
        assertEquals(Color(215, 159, 102), tint)
    }

    @Test
    fun `GIVEN a black cover, WHEN its tint is computed, THEN it stays black and nothing divides by zero`() {
        // Given
        val average = Color(0, 0, 0)

        // When
        val tint = ambientTintOf(average)

        // Then
        assertEquals(Color(0, 0, 0), tint)
    }

    @Test
    fun `GIVEN pixels of two colours, WHEN averaged, THEN the result is the middle colour`() {
        // Given
        val pixels = intArrayOf(0xFF000000.toInt() or 0x640000, 0xFF000000.toInt() or 0xC80000)

        // When
        val average = averageColorOf(pixels)

        // Then
        assertEquals(Color(150, 0, 0), average)
    }
}
