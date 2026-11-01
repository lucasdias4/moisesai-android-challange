package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiUnknownItem
import com.moisesai.challange.core.network.support.aTrack
import com.moisesai.challange.core.network.support.anAlbum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ApiResponseExtensionsTest {

    @Test
    fun `tracks keeps only the tracks, in their original order`() {
        // Given
        val response = ApiResponse(listOf(anAlbum(id = 1), aTrack(id = 10), ApiUnknownItem, aTrack(id = 11)))

        // When
        val tracks = response.tracks()

        // Then
        assertEquals(listOf(10L, 11L), tracks.map { it.id })
    }

    @Test
    fun `album returns the first album`() {
        // Given
        val response = ApiResponse(listOf(aTrack(id = 10), anAlbum(id = 1), anAlbum(id = 2)))

        // When
        val album = response.album()

        // Then
        assertEquals(1L, album?.id)
    }

    @Test
    fun `album is null when the response has none`() {
        // Given
        val response = ApiResponse(listOf(aTrack(id = 10), ApiUnknownItem))

        // When
        val album = response.album()

        // Then
        assertNull(album)
    }
}
