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
    fun `GIVEN a response with albums, tracks and an unknown item, WHEN reading the tracks, THEN only the tracks remain in their original order`() {
        // Given
        val response = ApiResponse(listOf(anAlbum(id = 1), aTrack(id = 10), ApiUnknownItem, aTrack(id = 11)))

        // When
        val tracks = response.getOnlyTracks()

        // Then
        assertEquals(listOf(10L, 11L), tracks.map { it.id })
    }

    @Test
    fun `GIVEN a response with several albums, WHEN reading the album, THEN the first album is returned`() {
        // Given
        val response = ApiResponse(listOf(aTrack(id = 10), anAlbum(id = 1), anAlbum(id = 2)))

        // When
        val album = response.getOnlyAlbum()

        // Then
        assertEquals(1L, album?.id)
    }

    @Test
    fun `GIVEN a response without an album, WHEN reading the album, THEN it is null`() {
        // Given
        val response = ApiResponse(listOf(aTrack(id = 10), ApiUnknownItem))

        // When
        val album = response.getOnlyAlbum()

        // Then
        assertNull(album)
    }
}
