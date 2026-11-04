package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ID
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTrackUseCaseTest {

    private val trackRepository = mockk<TrackRepository>()
    private val getTrack = GetTrackUseCase(trackRepository)

    @Test
    fun `GIVEN a track the repository has, WHEN getting it, THEN it is returned`() = runTest {
        // Given
        val track = aTrackDomainModel(id = LEVITATING_ID)
        coEvery { trackRepository.getTrack(LEVITATING_ID) } returns Result.success(track)

        // When
        val result = getTrack(LEVITATING_ID)

        // Then
        assertEquals(track, result.getOrThrow())
    }

    @Test
    fun `GIVEN a track the repository fails to get, WHEN getting it, THEN it fails with the same error`() = runTest {
        // Given
        val networkError = AppError.Network()
        coEvery { trackRepository.getTrack(LEVITATING_ID) } returns Result.failure(networkError)

        // When
        val result = getTrack(LEVITATING_ID)

        // Then
        assertEquals(networkError, result.exceptionOrNull())
    }
}
