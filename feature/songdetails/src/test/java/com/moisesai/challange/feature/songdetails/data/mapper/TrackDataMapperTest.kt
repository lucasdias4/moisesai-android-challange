package com.moisesai.challange.feature.songdetails.data.mapper

import com.moisesai.challange.feature.songdetails.fixture.aDbTrack
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songdetails.fixture.anApiTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackDataMapperTest {

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
    fun `GIVEN an api track, WHEN mapped to the domain, THEN every field is kept`() {
        // Given
        val apiTrack = anApiTrack()

        // When
        val domainTrack = apiTrack.toDomain()

        // Then
        assertEquals(aTrackDomainModel(), domainTrack)
    }

    @Test
    fun `GIVEN a saved track, WHEN mapped to the domain, THEN every field is kept`() {
        // Given
        val dbTrack = aDbTrack()

        // When
        val domainTrack = dbTrack.toDomain()

        // Then
        assertEquals(aTrackDomainModel(), domainTrack)
    }

    @Test
    fun `GIVEN a domain track, WHEN mapped to the database, THEN every field is kept`() {
        // Given
        val domainTrack = aTrackDomainModel()

        // When
        val dbTrack = domainTrack.toDb()

        // Then
        assertEquals(aDbTrack(), dbTrack)
    }

    @Test
    fun `GIVEN an api track without cover, preview and release date, WHEN mapped to the domain, THEN they stay absent`() {
        // Given
        val apiTrack = anApiTrack().copy(artUrl = null, previewUrl = null, releaseDate = null)

        // When
        val domainTrack = apiTrack.toDomain()

        // Then
        assertNull(domainTrack.artUrl)
        assertNull(domainTrack.previewUrl)
        assertNull(domainTrack.releaseDate)
    }
}
