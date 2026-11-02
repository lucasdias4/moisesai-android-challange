package com.moisesai.challange.core.design.graphics

import org.junit.Assert.assertEquals
import org.junit.Test

class CssBoxShadowTest {

    @Test
    fun `GIVEN a css blur of 50 pixels, WHEN converted, THEN the blur radius is the one whose gaussian has a deviation of 25`() {
        // Given
        val cssBlur = 50f

        // When
        val radius = cssBlurToBlurRadius(cssBlur)

        // Then
        assertEquals(25f, 0.57735f * radius + 0.5f, 0.01f)
    }

    @Test
    fun `GIVEN no blur, WHEN converted, THEN the radius is zero`() {
        // Given
        val cssBlur = 0f

        // When
        val radius = cssBlurToBlurRadius(cssBlur)

        // Then
        assertEquals(0f, radius, 0f)
    }
}
