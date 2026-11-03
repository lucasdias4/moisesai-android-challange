package com.moisesai.challange.feature.songdetails.presentation.mapper

import com.moisesai.challange.core.player.PlaybackState
import com.moisesai.challange.feature.songdetails.fixture.aPausedState
import com.moisesai.challange.feature.songdetails.fixture.aPlayingState
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayingTrackUiMapperTest {

    private val playedFor7Of30Seconds = aPlayingState(positionMillis = 7_400L, durationMillis = 30_000L)

    @Test
    fun `GIVEN a track of an album, WHEN mapped to ui, THEN the names and the album id are kept`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val playingTrack = track.toUiPlayingTrack(PlaybackState.Idle)

        // Then
        assertEquals(track.name, playingTrack.trackName)
        assertEquals(track.albumId, playingTrack.albumId)
    }

    @Test
    fun `GIVEN a track played for 7 of 30 seconds, WHEN mapped to ui, THEN the elapsed time is 0 colon 07`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val playingTrack = track.toUiPlayingTrack(playedFor7Of30Seconds)

        // Then
        assertEquals("0:07", playingTrack.playedTimeText)
    }

    @Test
    fun `GIVEN a track played for 7400 of 30000 milliseconds, WHEN mapped to ui, THEN the remaining time is rounded up to minus 0 colon 23`() {
        // Given
        val track = aTrackDomainModel()

        // When
        val playingTrack = track.toUiPlayingTrack(playedFor7Of30Seconds)

        // Then
        assertEquals("-0:23", playingTrack.remainingTimeText)
    }

    @Test
    fun `GIVEN a track played for a minute and 26 seconds, WHEN mapped to ui, THEN the elapsed time is 1 colon 26`() {
        // Given
        val playbackState = aPausedState(positionMillis = 86_000L, durationMillis = 240_000L)

        // When
        val playingTrack = aTrackDomainModel().toUiPlayingTrack(playbackState)

        // Then
        assertEquals("1:26", playingTrack.playedTimeText)
        assertEquals("-2:34", playingTrack.remainingTimeText)
    }

    @Test
    fun `GIVEN a track played for 15 of 30 seconds, WHEN mapped to ui, THEN the progress is one half`() {
        // Given
        val playbackState = aPausedState(positionMillis = 15_000L, durationMillis = 30_000L)

        // When
        val playingTrack = aTrackDomainModel().toUiPlayingTrack(playbackState)

        // Then
        assertEquals(0.5f, playingTrack.progress, 0f)
    }

    @Test
    fun `GIVEN audio whose length is not known yet, WHEN mapped to ui, THEN nothing has played`() {
        // Given
        val playbackState = PlaybackState.Idle

        // When
        val playingTrack = aTrackDomainModel().toUiPlayingTrack(playbackState)

        // Then
        assertEquals(0f, playingTrack.progress, 0f)
        assertEquals("0:00", playingTrack.playedTimeText)
        assertEquals("-0:00", playingTrack.remainingTimeText)
    }

    @Test
    fun `GIVEN a track with several artists, WHEN mapped to ui, THEN the first artist and the 600 pixel cover are shown`() {
        // Given
        val track = aTrackDomainModel(artistName = "Justice, Tame Impala, SZA")

        // When
        val playingTrack = track.toUiPlayingTrack(PlaybackState.Idle)

        // Then
        assertEquals("Justice", playingTrack.artistName)
        assertEquals(track.artUrl!!.replace("100x100bb", "600x600bb"), playingTrack.artUrl)
    }

    @Test
    fun `GIVEN a track that is playing on repeat, WHEN mapped to ui, THEN the ui says so`() {
        // Given
        val playbackState = aPlayingState(isRepeatEnabled = true)

        // When
        val playingTrack = aTrackDomainModel().toUiPlayingTrack(playbackState)

        // Then
        assertTrue(playingTrack.isPlaying)
        assertTrue(playingTrack.isRepeatEnabled)
    }
}
