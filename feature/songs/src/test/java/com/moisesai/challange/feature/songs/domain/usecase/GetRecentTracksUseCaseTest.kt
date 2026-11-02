package com.moisesai.challange.feature.songs.domain.usecase

import app.cash.turbine.test
import com.moisesai.challange.feature.songs.domain.repository.RecentTracksRepository
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRecentTracksUseCaseTest {

    private val repository = mockk<RecentTracksRepository>()
    private val getRecentTracks = GetRecentTracksUseCase(repository)

    @Test
    fun `GIVEN recent tracks, WHEN getting them, THEN the repository tracks are emitted`() = runTest {
        // Given
        every { repository.observeRecentTracks(any()) } returns flowOf(Result.success(listOf(aTrackDomainModel())))

        // When
        val emissions = getRecentTracks()

        // Then
        emissions.test {
            assertEquals(listOf(aTrackDomainModel()), awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN any repository, WHEN getting recent tracks, THEN the repository is asked for ten tracks`() = runTest {
        // Given
        every { repository.observeRecentTracks(any()) } returns flowOf(Result.success(emptyList()))

        // When
        getRecentTracks().test { awaitItem(); awaitComplete() }

        // Then
        verify { repository.observeRecentTracks(EXPECTED_LIMIT) }
    }

    private companion object {
        const val EXPECTED_LIMIT = 10
    }
}
