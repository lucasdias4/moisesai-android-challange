package com.moisesai.challange.feature.album.presentation.mapper

import com.moisesai.challange.feature.album.fixture.ALBUM_ART_URL
import com.moisesai.challange.feature.album.fixture.ALBUM_NAME
import com.moisesai.challange.feature.album.fixture.FIRST_TRACK_ID
import com.moisesai.challange.feature.album.fixture.anAlbumWithTracksDomainModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumUiMapperTest {

    @Test
    fun `GIVEN an album with an iTunes cover, WHEN mapped to ui, THEN the header cover is 600 pixels and the row cover is 200`() {
        // Given
        val albumWithTracks = anAlbumWithTracksDomainModel()

        // When
        val uiAlbum = albumWithTracks.toUiModel()

        // Then
        assertEquals(ALBUM_ART_URL.replace("100x100bb", "600x600bb"), uiAlbum.artUrl)
        assertEquals(ALBUM_ART_URL.replace("100x100bb", "200x200bb"), uiAlbum.tracks.single().artUrl)
    }

    @Test
    fun `GIVEN a track with several artists, WHEN mapped to ui, THEN the row shows the first artist`() {
        // Given
        val albumWithTracks = anAlbumWithTracksDomainModel()

        // When
        val uiAlbum = albumWithTracks.toUiModel()

        // Then
        assertEquals("Chris Stapleton", uiAlbum.tracks.single().artistName)
    }

    @Test
    fun `GIVEN an album, WHEN mapped to ui, THEN the album name and the track id are kept`() {
        // Given
        val albumWithTracks = anAlbumWithTracksDomainModel()

        // When
        val uiAlbum = albumWithTracks.toUiModel()

        // Then
        assertEquals(ALBUM_NAME, uiAlbum.name)
        assertEquals(FIRST_TRACK_ID, uiAlbum.tracks.single().id)
    }
}
