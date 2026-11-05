package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.SavedSearchDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbSavedSearchTrack
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.runsWriteTransactionsInline
import com.moisesai.challange.core.database.support.stopStubbingWriteTransactions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SavedSearchLocalDataSourceTest {

    private val database = mockk<AppDatabase>()
    private val savedSearchDao = mockk<SavedSearchDao>(relaxed = true)
    private val trackDao = mockk<TrackDao>(relaxed = true)
    private val dataSource = SavedSearchLocalDataSource(database, savedSearchDao, trackDao)

    private val savedSearch = DbSavedSearch(searchText = "dua lipa", fetchedAtMillis = 1_000)
    private val tracks = listOf(aDbTrack(id = 11), aDbTrack(id = 12), aDbTrack(id = 13))

    @Before
    fun setUp() {
        database.runsWriteTransactionsInline()
    }

    @After
    fun tearDown() {
        stopStubbingWriteTransactions()
    }

    @Test
    fun `GIVEN a saved search, WHEN getting it, THEN the result is a success with the search`() = runTest {
        // Given
        coEvery { savedSearchDao.getSavedSearch("dua lipa") } returns savedSearch

        // When
        val result = dataSource.getSavedSearch(searchText = "dua lipa")

        // Then
        assertEquals(savedSearch, result.getOrThrow())
    }

    @Test
    fun `GIVEN a search that was never saved, WHEN getting it, THEN the result is a success with no search`() = runTest {
        // Given
        coEvery { savedSearchDao.getSavedSearch(any()) } returns null

        // When
        val result = dataSource.getSavedSearch(searchText = "dua lipa")

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `GIVEN a saved search, WHEN getting a page of its tracks, THEN the page asked for is returned`() = runTest {
        // Given
        coEvery { savedSearchDao.getSavedSearchTracksPage("dua lipa", limit = 20, offset = 40) } returns tracks

        // When
        val result = dataSource.getSavedSearchTracksPage(searchText = "dua lipa", limit = 20, offset = 40)

        // Then
        assertEquals(tracks, result.getOrThrow())
    }

    @Test
    fun `GIVEN a search that was never saved, WHEN getting a page of its tracks, THEN the result is a success with an empty list`() =
        runTest {
            // Given
            coEvery { savedSearchDao.getSavedSearchTracksPage(any(), any(), any()) } returns emptyList()

            // When
            val result = dataSource.getSavedSearchTracksPage(searchText = "dua lipa", limit = 20, offset = 0)

            // Then
            assertTrue(result.getOrThrow().isEmpty())
        }

    @Test
    fun `GIVEN a database that fails, WHEN getting a page of tracks, THEN the result is an unknown error`() = runTest {
        // Given
        coEvery { savedSearchDao.getSavedSearchTracksPage(any(), any(), any()) } throws IllegalStateException("database is closed")

        // When
        val error = dataSource.getSavedSearchTracksPage(searchText = "dua lipa", limit = 20, offset = 0).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN a search and its tracks, WHEN saving it, THEN the rows keep the order of the tracks`() = runTest {
        // Given
        val expectedRows = listOf(
            DbSavedSearchTrack(searchText = "dua lipa", position = 0, trackId = 11),
            DbSavedSearchTrack(searchText = "dua lipa", position = 1, trackId = 12),
            DbSavedSearchTrack(searchText = "dua lipa", position = 2, trackId = 13),
        )

        // When
        val result = dataSource.saveSearch(savedSearch, tracks)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { savedSearchDao.insertSavedSearchTracks(expectedRows) }
    }

    @Test
    fun `GIVEN a search and its tracks, WHEN saving it, THEN the earlier save goes first and the rows go in last`() = runTest {
        // When
        dataSource.saveSearch(savedSearch, tracks)

        // Then
        coVerifyOrder {
            savedSearchDao.deleteSavedSearch("dua lipa")
            trackDao.upsertTracks(tracks)
            savedSearchDao.upsertSavedSearch(savedSearch)
            savedSearchDao.insertSavedSearchTracks(any())
        }
    }

    @Test
    fun `GIVEN a search without tracks, WHEN saving it, THEN the search is saved with no rows`() = runTest {
        // Given
        val noTracks = emptyList<DbTrack>()

        // When
        val result = dataSource.saveSearch(savedSearch, noTracks)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { savedSearchDao.upsertSavedSearch(savedSearch) }
        coVerify(exactly = 1) { savedSearchDao.insertSavedSearchTracks(emptyList()) }
    }

    @Test
    fun `GIVEN tracks that cannot be saved, WHEN saving a search, THEN the result is an unknown error and the search is not saved`() =
        runTest {
            // Given
            coEvery { trackDao.upsertTracks(any()) } throws IllegalStateException("disk is full")

            // When
            val error = dataSource.saveSearch(savedSearch, tracks).exceptionOrNull()

            // Then
            assertTrue(error is AppError.Unknown)
            coVerify(exactly = 0) { savedSearchDao.upsertSavedSearch(any()) }
            coVerify(exactly = 0) { savedSearchDao.insertSavedSearchTracks(any()) }
        }
}
