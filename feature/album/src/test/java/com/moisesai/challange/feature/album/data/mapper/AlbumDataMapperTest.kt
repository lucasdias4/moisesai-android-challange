package com.moisesai.challange.feature.album.data.mapper

import com.moisesai.challange.feature.album.fixture.aDbAlbum
import com.moisesai.challange.feature.album.fixture.aDbTrack
import com.moisesai.challange.feature.album.fixture.aTrackDomainModel
import com.moisesai.challange.feature.album.fixture.anAlbumDomainModel
import com.moisesai.challange.feature.album.fixture.anApiAlbum
import com.moisesai.challange.feature.album.fixture.anApiTrack
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumDataMapperTest {

    @Test
    fun `GIVEN an api album, WHEN mapped to the database, THEN its id, names and cover are kept`() {
        // Given
        val apiAlbum = anApiAlbum()

        // When
        val dbAlbum = apiAlbum.toDb()

        // Then
        assertEquals(aDbAlbum(), dbAlbum)
    }

    @Test
    fun `GIVEN an api track, WHEN mapped to the database, THEN every field is kept`() {
        // Given
        val apiTrack = anApiTrack()

        // When
        val dbTrack = apiTrack.toDb()

        // Then
        assertEquals(aDbTrack(), dbTrack)
    }

    @Test
    fun `GIVEN a saved album, WHEN mapped to the domain, THEN its id, names and cover are kept`() {
        // Given
        val dbAlbum = aDbAlbum()

        // When
        val album = dbAlbum.toDomain()

        // Then
        assertEquals(anAlbumDomainModel(), album)
    }

    @Test
    fun `GIVEN a saved track, WHEN mapped to the domain, THEN every field is kept`() {
        // Given
        val dbTrack = aDbTrack()

        // When
        val track = dbTrack.toDomain()

        // Then
        assertEquals(aTrackDomainModel(), track)
    }
}
