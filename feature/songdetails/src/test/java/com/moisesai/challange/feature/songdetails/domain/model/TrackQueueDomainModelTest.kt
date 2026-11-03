package com.moisesai.challange.feature.songdetails.domain.model

import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackQueueDomainModelTest {

    private val first = aTrackDomainModel(id = 1)
    private val second = aTrackDomainModel(id = 2)
    private val third = aTrackDomainModel(id = 3)

    @Test
    fun `GIVEN an album, WHEN the queue starts at its second track, THEN the second track is the current one`() {
        // Given
        val albumTracks = listOf(first, second, third)

        // When
        val queue = TrackQueueDomainModel.startingAt(chosenTrack = second, albumTracks = albumTracks)

        // Then
        assertEquals(second, queue.currentTrack)
        assertEquals(albumTracks, queue.tracks)
    }

    @Test
    fun `GIVEN an album where the chosen track is missing, WHEN the queue starts at it, THEN the queue is only the chosen track`() {
        // Given
        val albumTracks = listOf(first, third)

        // When
        val queue = TrackQueueDomainModel.startingAt(chosenTrack = second, albumTracks = albumTracks)

        // Then
        assertEquals(listOf(second), queue.tracks)
    }

    @Test
    fun `GIVEN an album with a track that has no audio, WHEN the queue starts at another track, THEN the track without audio is left out`() {
        // Given
        val withoutAudio = aTrackDomainModel(id = 4, previewUrl = null)

        // When
        val queue = TrackQueueDomainModel.startingAt(chosenTrack = first, albumTracks = listOf(first, withoutAudio, second))

        // Then
        assertEquals(listOf(first, second), queue.tracks)
    }

    @Test
    fun `GIVEN a chosen track that has no audio, WHEN the queue starts at it, THEN the chosen track stays in the queue`() {
        // Given
        val withoutAudio = aTrackDomainModel(id = 4, previewUrl = null)

        // When
        val queue = TrackQueueDomainModel.startingAt(chosenTrack = withoutAudio, albumTracks = listOf(first, withoutAudio))

        // Then
        assertEquals(withoutAudio, queue.currentTrack)
    }

    @Test
    fun `GIVEN a queue on its first track, WHEN asking for the neighbours, THEN there is a next track and no previous track`() {
        // Given
        val queue = TrackQueueDomainModel(listOf(first, second), currentIndex = 0)

        // When
        val hasNext = queue.hasNextTrack
        val hasPrevious = queue.hasPreviousTrack

        // Then
        assertTrue(hasNext)
        assertFalse(hasPrevious)
    }

    @Test
    fun `GIVEN a queue on its first track, WHEN moved to the next track, THEN the second track is the current one`() {
        // Given
        val queue = TrackQueueDomainModel(listOf(first, second), currentIndex = 0)

        // When
        val moved = queue.movedToNextTrack()

        // Then
        assertEquals(second, moved.currentTrack)
    }

    @Test
    fun `GIVEN a queue on its last track, WHEN moved to the next track, THEN the queue stays as it was`() {
        // Given
        val queue = TrackQueueDomainModel(listOf(first, second), currentIndex = 1)

        // When
        val moved = queue.movedToNextTrack()

        // Then
        assertSame(queue, moved)
    }

    @Test
    fun `GIVEN a queue on its second track, WHEN moved to the previous track, THEN the first track is the current one`() {
        // Given
        val queue = TrackQueueDomainModel(listOf(first, second), currentIndex = 1)

        // When
        val moved = queue.movedToPreviousTrack()

        // Then
        assertEquals(first, moved.currentTrack)
    }

    @Test
    fun `GIVEN a queue on its first track, WHEN moved to the previous track, THEN the queue stays as it was`() {
        // Given
        val queue = TrackQueueDomainModel(listOf(first, second), currentIndex = 0)

        // When
        val moved = queue.movedToPreviousTrack()

        // Then
        assertSame(queue, moved)
    }
}
