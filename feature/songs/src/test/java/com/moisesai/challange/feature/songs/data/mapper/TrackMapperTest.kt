package com.moisesai.challange.feature.songs.data.mapper

import com.moisesai.challange.feature.songs.fixture.aDbTrack
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.fixture.anApiTrack
import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private fun anApiAlbum() = ApiAlbum(
    id = 1538003494L,
    name = "Future Nostalgia",
    artistName = "Dua Lipa",
    artUrl = null,
    releaseDate = "2020-03-27T12:00:00Z",
    trackCount = 11,
)

class TrackMapperTest {

    @Test
    fun `GIVEN an api track, WHEN mapped to db, THEN every field is carried over`() {
        // Given
        val apiTrack = anApiTrack()

        // When
        val dbTrack = apiTrack.toDb()

        // Then
        assertEquals(aDbTrack(), dbTrack)
    }

    @Test
    fun `GIVEN a db track, WHEN mapped to domain, THEN every field is carried over`() {
        // Given
        val dbTrack = aDbTrack()

        // When
        val domainTrack = dbTrack.toDomain()

        // Then
        assertEquals(aTrackDomainModel(), domainTrack)
    }

    @Test
    fun `GIVEN a db track without artwork, WHEN mapped to domain, THEN the artwork url stays null`() {
        // Given
        val dbTrack = aDbTrack(artUrl = null)

        // When
        val domainTrack = dbTrack.toDomain()

        // Then
        assertNull(domainTrack.artUrl)
    }

    @Test
    fun `GIVEN one row more than the page size, WHEN mapped to a page, THEN the page is full and has a next page`() {
        // Given
        val rows = listOf(aDbTrack(id = 1), aDbTrack(id = 2), aDbTrack(id = 3))

        // When
        val page = rows.toTrackPageDomainModel(pageSize = 2)

        // Then
        assertEquals(listOf(aTrackDomainModel(id = 1), aTrackDomainModel(id = 2)), page.tracks)
        assertTrue(page.hasNext)
    }

    @Test
    fun `GIVEN as many rows as the page size, WHEN mapped to a page, THEN all rows are in it and there is no next page`() {
        // Given
        val rows = listOf(aDbTrack(id = 1), aDbTrack(id = 2))

        // When
        val page = rows.toTrackPageDomainModel(pageSize = 2)

        // Then
        assertEquals(2, page.tracks.size)
        assertFalse(page.hasNext)
    }

    @Test
    fun `GIVEN a response with a track and an album, WHEN mapped to db tracks, THEN only the track is mapped`() {
        // Given
        val response = ApiResponse(results = listOf(anApiAlbum(), anApiTrack()))

        // When
        val dbTracks = response.toDbTracks()

        // Then
        assertEquals(listOf(aDbTrack()), dbTracks)
    }
}
