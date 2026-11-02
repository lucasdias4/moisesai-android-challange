package com.moisesai.challange.core.design.animation

import androidx.compose.animation.core.Easing
import org.junit.Assert.assertEquals
import org.junit.Test

// The expected values were sampled from Chromium running the design's own CSS easings at every tenth of a second,
// so the Compose curves are held to what the design really does.
class AuroraEasingTest {

    private val tolerance = 0.005f

    private fun assertSamples(easing: Easing, expected: List<Float>) {
        expected.forEachIndexed { index, value ->
            assertEquals("at ${index * 10}%", value, easing.transform(index / 10f), tolerance)
        }
    }

    @Test
    fun `GIVEN the spring curve, WHEN sampled every tenth, THEN it overshoots one and settles like the design`() {
        // Given
        val chromium = listOf(0f, 0.6022f, 0.9186f, 1.0453f, 1.079f, 1.0735f, 1.0542f, 1.0329f, 1.0152f, 1.0039f, 1f)

        // When / Then
        assertSamples(SpringOvershootEasing, chromium)
    }

    @Test
    fun `GIVEN the ambient curve, WHEN sampled every tenth, THEN it matches the design`() {
        // Given
        val chromium = listOf(0f, 0.4343f, 0.7254f, 0.8626f, 0.9283f, 0.9626f, 0.9813f, 0.9916f, 0.997f, 0.9994f, 1f)

        // When / Then
        assertSamples(AmbientEasing, chromium)
    }

    @Test
    fun `GIVEN the css ease curve, WHEN sampled every tenth, THEN it matches the design`() {
        // Given
        val chromium = listOf(0f, 0.0948f, 0.2952f, 0.5133f, 0.6825f, 0.8024f, 0.8852f, 0.9408f, 0.9756f, 0.9943f, 1f)

        // When / Then
        assertSamples(CssEaseEasing, chromium)
    }

    @Test
    fun `GIVEN the css ease in out curve, WHEN sampled every tenth, THEN it matches the design`() {
        // Given
        val chromium = listOf(0f, 0.0197f, 0.0817f, 0.1874f, 0.3319f, 0.5f, 0.6681f, 0.8126f, 0.9183f, 0.9803f, 1f)

        // When / Then
        assertSamples(CssEaseInOutEasing, chromium)
    }
}
