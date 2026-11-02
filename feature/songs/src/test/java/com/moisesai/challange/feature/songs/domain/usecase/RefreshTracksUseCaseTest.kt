package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songs.domain.model.Constants
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RefreshTracksUseCaseTest {

    private val repository = mockk<TrackSearchRepository>()
    private val refreshTracks = RefreshTracksUseCase(repository)

    @Test
    fun `GIVEN a search text, WHEN refreshing, THEN the repository is asked to refresh it with the page size`() = runTest {
        // Given
        val expectedPage = TrackPageDomainModel(listOf(aTrackDomainModel()), hasNext = false)
        coEvery { repository.refreshTracksBySearchText(any(), any()) } returns Result.success(expectedPage)

        // When
        val page = refreshTracks("levitating").getOrThrow()

        // Then
        assertEquals(expectedPage, page)
        coVerify { repository.refreshTracksBySearchText("levitating", Constants.SEARCH_PAGE_SIZE) }
    }

    @Test
    fun `GIVEN the repository fails, WHEN refreshing, THEN the same failure is returned`() = runTest {
        // Given
        val error = AppError.Network()
        coEvery { repository.refreshTracksBySearchText(any(), any()) } returns Result.failure(error)

        // When
        val result = refreshTracks("levitating")

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }
}
