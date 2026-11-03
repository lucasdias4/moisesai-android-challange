package com.moisesai.challange.core.design.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class CoverUrlExtensionTest {

    private val searchCoverUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/190295092665.jpg/100x100bb.jpg"

    @Test
    fun `GIVEN an iTunes cover url, WHEN asking for the medium cover, THEN the url asks for 200 pixels`() {
        // Given
        val coverUrl = searchCoverUrl

        // When
        val mediumCoverUrl = coverUrl.toMediumCoverUrl()

        // Then
        assertEquals("https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/190295092665.jpg/200x200bb.jpg", mediumCoverUrl)
    }

    @Test
    fun `GIVEN an iTunes cover url, WHEN asking for the large cover, THEN the url asks for 600 pixels`() {
        // Given
        val coverUrl = searchCoverUrl

        // When
        val largeCoverUrl = coverUrl.toLargeCoverUrl()

        // Then
        assertEquals("https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/190295092665.jpg/600x600bb.jpg", largeCoverUrl)
    }
}
