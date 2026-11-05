package com.moisesai.challange.core.database.datasource

import app.cash.turbine.test
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.RecentlyPlayedTrackDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbRecentlyPlayedTrack
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.runsWriteTransactionsInline
import com.moisesai.challange.core.database.support.stopStubbingWriteTransactions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecentlyPlayedTrackLocalDataSourceTest {

    private val database = mockk<AppDatabase>()
    private val recentlyPlayedTrackDao = mockk<RecentlyPlayedTrackDao>(relaxed = true)
    private val trackDao = mockk<TrackDao>(relaxed = true)
    private val dataSource = RecentlyPlayedTrackLocalDataSource(database, recentlyPlayedTrackDao, trackDao)

    @Before
    fun setUp() {
        database.runsWriteTransactionsInline()
    }

    @After
    fun tearDown() {
        stopStubbingWriteTransactions()
    }

    @Test
    fun `GIVEN played tracks, WHEN observing the recent ones, THEN each list the database emits arrives as a success`() = runTest {
        // Given
        val first = listOf(aDbTrack(id = 1))
        val second = listOf(aDbTrack(id = 2), aDbTrack(id = 1))
        every { recentlyPlayedTrackDao.observeRecentlyPlayedTracks(limit = 5) } returns flowOf(first, second)

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 5)

        // Then
        recentTracks.test {
            assertEquals(first, awaitItem().getOrThrow())
            assertEquals(second, awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a database that fails when the flow is built, WHEN observing the recent tracks, THEN a failure is emitted instead of an exception`() =
        runTest {
            // Given
            every { recentlyPlayedTrackDao.observeRecentlyPlayedTracks(any()) } throws IllegalStateException("database is closed")

            // When
            val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 5)

            // Then
            recentTracks.test {
                assertTrue(awaitItem().exceptionOrNull() is AppError.Unknown)
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN a track, WHEN saving it as played, THEN the track is saved first and then marked as played at that time`() = runTest {
        // Given
        val track = aDbTrack(id = 7)

        // When
        val result = dataSource.saveRecentlyPlayedTrack(track, playedAtMillis = 5_000)

        // Then
        assertTrue(result.isSuccess)
        coVerifyOrder {
            trackDao.upsertTrack(track)
            recentlyPlayedTrackDao.upsertRecentlyPlayedTrack(DbRecentlyPlayedTrack(trackId = 7, playedAtMillis = 5_000))
        }
    }

    @Test
    fun `GIVEN a track that cannot be saved, WHEN saving it as played, THEN the result is an unknown error and it is not marked as played`() =
        runTest {
            // Given
            coEvery { trackDao.upsertTrack(any()) } throws IllegalStateException("disk is full")

            // When
            val error = dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 7), playedAtMillis = 5_000).exceptionOrNull()

            // Then
            assertTrue(error is AppError.Unknown)
            coVerify(exactly = 0) { recentlyPlayedTrackDao.upsertRecentlyPlayedTrack(any()) }
        }
}
