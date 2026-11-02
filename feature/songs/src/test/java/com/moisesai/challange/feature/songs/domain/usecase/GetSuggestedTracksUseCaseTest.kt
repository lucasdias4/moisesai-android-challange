package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songs.domain.repository.SuggestedTracksRepository
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSuggestedTracksUseCaseTest {

    private val repository = mockk<SuggestedTracksRepository>()
    private val getSuggestedTracks = GetSuggestedTracksUseCase(repository)

    @Test
    fun `GIVEN the repository has the tracks, WHEN getting suggestions, THEN they are returned`() = runTest {
        // Given
        coEvery { repository.getSuggestedTracks(any()) } returns Result.success(listOf(aTrackDomainModel()))

        // When
        val tracks = getSuggestedTracks().getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel()), tracks)
    }

    @Test
    fun `GIVEN any repository, WHEN getting suggestions, THEN the three hand picked songs are asked for in order`() = runTest {
        // Given
        coEvery { repository.getSuggestedTracks(any()) } returns Result.success(emptyList())

        // When
        getSuggestedTracks()

        // Then
        coVerify { repository.getSuggestedTracks(listOf(1440788744L, 1440899467L, 6827658231L)) }
    }

    @Test
    fun `GIVEN the repository fails, WHEN getting suggestions, THEN the same failure is returned`() = runTest {
        // Given
        val error = AppError.Network()
        coEvery { repository.getSuggestedTracks(any()) } returns Result.failure(error)

        // When
        val result = getSuggestedTracks()

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }
}
