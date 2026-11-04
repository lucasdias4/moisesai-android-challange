package com.moisesai.challange.feature.songdetails.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.common.TimeProvider
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.feature.songdetails.fixture.aDbTrack
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentTracksRepositoryImplTest {

    private val localDataSource = mockk<RecentlyPlayedTrackLocalDataSource>()
    private val playedAtMillis = 1_700_000_000_000L
    private val timeProvider = TimeProvider { playedAtMillis }
    private val repository = RecentTracksRepositoryImpl(localDataSource, timeProvider)

    @Test
    fun `GIVEN a track that was played, WHEN saving it, THEN the saved track and the time of now are given to the local source`() = runTest {
        // Given
        coEvery { localDataSource.saveRecentlyPlayedTrack(any(), any()) } returns Result.success(Unit)

        // When
        val result = repository.saveRecentTrack(aTrackDomainModel())

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { localDataSource.saveRecentlyPlayedTrack(aDbTrack(), playedAtMillis) }
    }

    @Test
    fun `GIVEN a local source that fails, WHEN saving a played track, THEN the same failure is returned`() = runTest {
        // Given
        val error = AppError.Unknown()
        coEvery { localDataSource.saveRecentlyPlayedTrack(any(), any()) } returns Result.failure(error)

        // When
        val result = repository.saveRecentTrack(aTrackDomainModel())

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }
}
