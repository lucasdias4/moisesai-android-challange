package com.moisesai.challange.core.network

import com.moisesai.challange.core.network.extension.album
import com.moisesai.challange.core.network.extension.tracks
import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.network.model.ApiUnknownItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ApiResponseExtensionsTest {

    @Test
    fun `tracks keeps only the tracks, in their original order`() {
        val response = ApiResponse(listOf(album(id = 1), track(id = 10), ApiUnknownItem, track(id = 11)))

        assertEquals(listOf(10L, 11L), response.tracks().map { it.id })
    }

    @Test
    fun `album returns the first album`() {
        val response = ApiResponse(listOf(track(id = 10), album(id = 1), album(id = 2)))

        assertEquals(1L, response.album()?.id)
    }

    @Test
    fun `album is null when the response has none`() {
        val response = ApiResponse(listOf(track(id = 10), ApiUnknownItem))

        assertNull(response.album())
    }

    private fun track(id: Long) = ApiTrack(
        id = id,
        name = "Track $id",
        artistName = "Artist",
        albumId = 1,
        albumName = "Album",
        artUrl = null,
        previewUrl = null,
        trackTimeMillis = 1000,
        releaseDate = null,
        trackNumber = 1,
    )

    private fun album(id: Long) = ApiAlbum(
        id = id,
        name = "Album $id",
        artistName = "Artist",
        artUrl = null,
        releaseDate = null,
        trackCount = 1,
    )
}
