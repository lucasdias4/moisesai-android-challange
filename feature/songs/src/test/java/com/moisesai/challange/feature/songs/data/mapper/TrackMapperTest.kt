package com.moisesai.challange.feature.songs.data.mapper

import com.moisesai.challange.feature.songs.fixture.aDbTrack
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.fixture.anApiTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
}
