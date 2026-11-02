package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.common.AppError
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

class SearchTracksUseCaseTest {

    private val repository = mockk<TrackSearchRepository>()
    private val searchTracks = SearchTracksUseCase(repository)

    @Test
    fun `GIVEN a blank search text, WHEN searching, THEN an empty page is returned and the repository is not asked`() = runTest {
        // Given
        val blankSearchText = "   "

        // When
        val page = searchTracks(blankSearchText, page = 0).getOrThrow()

        // Then
        assertEquals(TrackPageDomainModel(emptyList(), hasNext = false), page)
        coVerify(exactly = 0) { repository.searchTracksBySearchText(any(), any(), any()) }
    }

    @Test
    fun `GIVEN a search text and a page, WHEN searching, THEN the repository is asked for that page with the page size`() = runTest {
        // Given
        val expectedPage = TrackPageDomainModel(listOf(aTrackDomainModel()), hasNext = true)
        coEvery { repository.searchTracksBySearchText(any(), any(), any()) } returns Result.success(expectedPage)

        // When
        val page = searchTracks("levitating", page = 2).getOrThrow()

        // Then
        assertEquals(expectedPage, page)
        coVerify { repository.searchTracksBySearchText("levitating", 2, SEARCH_PAGE_SIZE) }
    }

    @Test
    fun `GIVEN the repository fails, WHEN searching, THEN the same failure is returned`() = runTest {
        // Given
        val error = AppError.Network()
        coEvery { repository.searchTracksBySearchText(any(), any(), any()) } returns Result.failure(error)

        // When
        val result = searchTracks("levitating", page = 0)

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }
}
