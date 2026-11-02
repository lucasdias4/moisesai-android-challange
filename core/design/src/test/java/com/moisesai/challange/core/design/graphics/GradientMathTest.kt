package com.moisesai.challange.core.design.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class GradientMathTest {

    private val delta = 0.01f

    @Test
    fun `GIVEN a 180 degree angle, WHEN the line is computed, THEN it runs straight down the full height`() {
        // Given
        val size = Size(200f, 100f)

        // When
        val line = cssGradientLine(180f, size)

        // Then
        assertEquals(100f, line.start.x, delta)
        assertEquals(0f, line.start.y, delta)
        assertEquals(100f, line.end.x, delta)
        assertEquals(100f, line.end.y, delta)
    }

    @Test
    fun `GIVEN a 90 degree angle, WHEN the line is computed, THEN it runs straight right across the full width`() {
        // Given
        val size = Size(200f, 100f)

        // When
        val line = cssGradientLine(90f, size)

        // Then
        assertEquals(Offset(0f, 50f), line.start)
        assertEquals(Offset(200f, 50f), line.end)
    }

    @Test
    fun `GIVEN a 140 degree angle on a wide box, WHEN the line is computed, THEN it is longer than the height and not corner to corner`() {
        // Given
        val size = Size(328f, 58f)

        // When
        val line = cssGradientLine(140f, size)

        // Then: the projected length is |w sin| + |h cos| and the line passes through the centre
        val expectedLength = 328f * 0.6428f + 58f * 0.766f
        assertEquals(expectedLength, (line.end - line.start).getDistance(), 0.1f)
        assertEquals(164f, (line.start.x + line.end.x) / 2f, delta)
        assertEquals(29f, (line.start.y + line.end.y) / 2f, delta)
    }
}
