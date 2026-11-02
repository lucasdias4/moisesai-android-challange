package com.moisesai.challange.feature.songs.presentation.mapper

import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackUiMapperTest {

    @Test
    fun `GIVEN a track of 203808 milliseconds, WHEN mapped to ui, THEN the duration is rounded to 3 minutes and 24 seconds`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("3:24", uiTrack.durationText)
    }

    @Test
    fun `GIVEN a track shorter than ten seconds past a minute, WHEN mapped to ui, THEN the seconds have two digits`() {
        // Given
        val track = aTrackDomainModel().copy(trackTimeMillis = 61_000L)

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("1:01", uiTrack.durationText)
    }

    @Test
    fun `GIVEN a track with an iTunes cover, WHEN mapped to ui, THEN the row cover is 200 pixels and the card cover is 600`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals(track.artUrl!!.replace("100x100bb", "200x200bb"), uiTrack.artUrl)
        assertEquals(track.artUrl!!.replace("100x100bb", "600x600bb"), uiTrack.largeArtUrl)
    }

    @Test
    fun `GIVEN a track of an album, WHEN mapped to ui, THEN the ui track keeps the album id`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals(track.albumId, uiTrack.albumId)
    }

    @Test
    fun `GIVEN a track without a cover, WHEN mapped to ui, THEN both cover urls are null`() {
        // Given
        val track = aTrackDomainModel(artUrl = null)

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertNull(uiTrack.artUrl)
        assertNull(uiTrack.largeArtUrl)
    }

    @Test
    fun `GIVEN a track, WHEN mapped to ui, THEN its identity and names are carried over`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals(track.id, uiTrack.id)
        assertEquals(track.name, uiTrack.name)
        assertEquals(track.artistName, uiTrack.artistName)
    }

    @Test
    fun `GIVEN a track a few milliseconds short of five minutes, WHEN mapped to ui, THEN the duration rounds up to 5 00`() {
        // Given: Let Down by Radiohead lasts 299560 milliseconds
        val track = aTrackDomainModel().copy(trackTimeMillis = 299_560L)

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("5:00", uiTrack.durationText)
    }

    @Test
    fun `GIVEN a name with a note in square brackets at the end, WHEN mapped to ui, THEN the note is dropped`() {
        // Given: the name iTunes gives for the Justice song
        val track = aTrackDomainModel().copy(name = "Flower In A Hurricane [From GTAVI: Original Score]")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("Flower In A Hurricane", uiTrack.name)
    }

    @Test
    fun `GIVEN a name with a note in parentheses at the end, WHEN mapped to ui, THEN the note is dropped`() {
        // Given
        val track = aTrackDomainModel().copy(name = "I Feel It Coming (feat. Daft Punk)")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("I Feel It Coming", uiTrack.name)
    }

    @Test
    fun `GIVEN a name with parentheses at the start, WHEN mapped to ui, THEN the name is kept whole`() {
        // Given
        val track = aTrackDomainModel().copy(name = "(What's the Story) Morning Glory?")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("(What's the Story) Morning Glory?", uiTrack.name)
    }

    @Test
    fun `GIVEN a name that is only a note, WHEN mapped to ui, THEN the name is kept whole`() {
        // Given
        val track = aTrackDomainModel().copy(name = "(Untitled)")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("(Untitled)", uiTrack.name)
    }

    @Test
    fun `GIVEN a song by several artists, WHEN mapped to ui, THEN only the first artist is shown`() {
        // Given: how iTunes lists the Justice song
        val track = aTrackDomainModel().copy(artistName = "Justice, Tame Impala, SZA & King Gizzard & The Lizard Wizard")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("Justice", uiTrack.artistName)
    }

    @Test
    fun `GIVEN a duo written with an ampersand, WHEN mapped to ui, THEN the whole name is kept`() {
        // Given
        val track = aTrackDomainModel().copy(artistName = "Daft Punk & Julian Casablancas")

        // When
        val uiTrack = track.toUiModel()

        // Then
        assertEquals("Daft Punk & Julian Casablancas", uiTrack.artistName)
    }
}
