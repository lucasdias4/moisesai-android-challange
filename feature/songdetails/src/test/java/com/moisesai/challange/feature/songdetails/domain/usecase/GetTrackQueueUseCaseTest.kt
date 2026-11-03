package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ALBUM_ID
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ID
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTrackQueueUseCaseTest {

    private val trackRepository = mockk<TrackRepository>()
    private val getTrackQueue = GetTrackQueueUseCase(trackRepository)

    private val chosenTrack = aTrackDomainModel(id = LEVITATING_ID)
    private val neighbour = aTrackDomainModel(id = 2)

    @Test
    fun `GIVEN a track whose album loads, WHEN loading the queue, THEN the queue is the album standing on the track`() = runTest {
        // Given
        coEvery { trackRepository.getTrack(LEVITATING_ID) } returns Result.success(chosenTrack)
        coEvery { trackRepository.getAlbumTracks(LEVITATING_ALBUM_ID) } returns Result.success(listOf(neighbour, chosenTrack))

        // When
        val queue = getTrackQueue(LEVITATING_ID).getOrThrow()

        // Then
        assertEquals(listOf(neighbour, chosenTrack), queue.tracks)
        assertEquals(chosenTrack, queue.currentTrack)
    }

    @Test
    fun `GIVEN a track whose album fails to load, WHEN loading the queue, THEN the queue is only the track`() = runTest {
        // Given
        coEvery { trackRepository.getTrack(LEVITATING_ID) } returns Result.success(chosenTrack)
        coEvery { trackRepository.getAlbumTracks(LEVITATING_ALBUM_ID) } returns Result.failure(AppError.Network())

        // When
        val queue = getTrackQueue(LEVITATING_ID).getOrThrow()

        // Then
        assertEquals(listOf(chosenTrack), queue.tracks)
    }

    @Test
    fun `GIVEN a track that fails to load, WHEN loading the queue, THEN it fails with the same error`() = runTest {
        // Given
        val networkError = AppError.Network()
        coEvery { trackRepository.getTrack(LEVITATING_ID) } returns Result.failure(networkError)

        // When
        val queue = getTrackQueue(LEVITATING_ID)

        // Then
        assertEquals(networkError, queue.exceptionOrNull())
    }
}
