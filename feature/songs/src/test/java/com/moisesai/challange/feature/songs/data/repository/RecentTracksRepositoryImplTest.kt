package com.moisesai.challange.feature.songs.data.repository

import app.cash.turbine.test
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.feature.songs.fixture.aDbTrack
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.support.aDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentTracksRepositoryImplTest {

    private val localDataSource = mockk<RecentlyPlayedTrackLocalDataSource>()
    private val repository = RecentTracksRepositoryImpl(localDataSource, aDispatcherProvider(UnconfinedTestDispatcher()))

    @Test
    fun `GIVEN the local source has recent tracks, WHEN observing them, THEN domain tracks are emitted`() = runTest {
        // Given
        every { localDataSource.observeRecentlyPlayedTracks(any()) } returns flowOf(Result.success(listOf(aDbTrack())))

        // When
        val emissions = repository.observeRecentTracks(RECENT_LIMIT)

        // Then
        emissions.test {
            assertEquals(listOf(aTrackDomainModel()), awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN the local source has no recent tracks, WHEN observing them, THEN an empty success is emitted`() = runTest {
        // Given
        every { localDataSource.observeRecentlyPlayedTracks(any()) } returns flowOf(Result.success(emptyList()))

        // When
        val emissions = repository.observeRecentTracks(RECENT_LIMIT)

        // Then
        emissions.test {
            assertEquals(emptyList<Any>(), awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN the local source fails, WHEN observing recent tracks, THEN the failure is emitted`() = runTest {
        // Given
        val error = AppError.Unknown()
        every { localDataSource.observeRecentlyPlayedTracks(any()) } returns flowOf(Result.failure(error))

        // When
        val emissions = repository.observeRecentTracks(RECENT_LIMIT)

        // Then
        emissions.test {
            assertTrue(awaitItem().exceptionOrNull() === error)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a limit, WHEN observing recent tracks, THEN the local source is asked for that limit`() = runTest {
        // Given
        every { localDataSource.observeRecentlyPlayedTracks(any()) } returns flowOf(Result.success(emptyList()))

        // When
        repository.observeRecentTracks(RECENT_LIMIT).test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify { localDataSource.observeRecentlyPlayedTracks(RECENT_LIMIT) }
    }

    private companion object {
        const val RECENT_LIMIT = 10
    }
}
