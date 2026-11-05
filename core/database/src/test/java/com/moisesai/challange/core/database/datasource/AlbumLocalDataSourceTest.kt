package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.AlbumDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbAlbumTrack
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.database.support.aDbAlbum
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

class AlbumLocalDataSourceTest {

    private val database = mockk<AppDatabase>()
    private val albumDao = mockk<AlbumDao>(relaxed = true)
    private val trackDao = mockk<TrackDao>(relaxed = true)
    private val dataSource = AlbumLocalDataSource(database, albumDao, trackDao)

    private val album = aDbAlbum(id = 100)
    private val tracks = listOf(aDbTrack(id = 21), aDbTrack(id = 22), aDbTrack(id = 23))

    @Before
    fun setUp() {
        database.runsWriteTransactionsInline()
    }

    @After
    fun tearDown() {
        stopStubbingWriteTransactions()
    }

    @Test
    fun `GIVEN a saved album, WHEN getting it, THEN the result is a success with the album`() = runTest {
        // Given
        coEvery { albumDao.getAlbum(100) } returns album

        // When
        val result = dataSource.getAlbum(albumId = 100)

        // Then
        assertEquals(album, result.getOrThrow())
    }

    @Test
    fun `GIVEN an album that was never saved, WHEN getting it, THEN the result is a success with no album`() = runTest {
        // Given
        coEvery { albumDao.getAlbum(any()) } returns null

        // When
        val result = dataSource.getAlbum(albumId = 100)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `GIVEN a saved album, WHEN getting its tracks, THEN the tracks come back in the order of the album`() = runTest {
        // Given
        coEvery { albumDao.getAlbumTracks(100) } returns tracks

        // When
        val result = dataSource.getAlbumTracks(albumId = 100)

        // Then
        assertEquals(tracks, result.getOrThrow())
    }

    @Test
    fun `GIVEN an album that was never saved, WHEN getting its tracks, THEN the result is a success with an empty list`() = runTest {
        // Given
        coEvery { albumDao.getAlbumTracks(any()) } returns emptyList()

        // When
        val result = dataSource.getAlbumTracks(albumId = 100)

        // Then
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `GIVEN a database that fails, WHEN getting an album, THEN the result is an unknown error`() = runTest {
        // Given
        coEvery { albumDao.getAlbum(any()) } throws IllegalStateException("database is closed")

        // When
        val error = dataSource.getAlbum(albumId = 100).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN an album and its tracks, WHEN saving it, THEN the rows keep the order of the tracks`() = runTest {
        // Given
        val expectedRows = listOf(
            DbAlbumTrack(albumId = 100, position = 0, trackId = 21),
            DbAlbumTrack(albumId = 100, position = 1, trackId = 22),
            DbAlbumTrack(albumId = 100, position = 2, trackId = 23),
        )

        // When
        val result = dataSource.saveAlbum(album, tracks)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { albumDao.insertAlbumTracks(expectedRows) }
    }

    @Test
    fun `GIVEN an album and its tracks, WHEN saving it, THEN the tracks go in first and the earlier rows are replaced`() = runTest {
        // When
        dataSource.saveAlbum(album, tracks)

        // Then
        coVerifyOrder {
            trackDao.upsertTracks(tracks)
            albumDao.upsertAlbum(album)
            albumDao.deleteAlbumTracks(100)
            albumDao.insertAlbumTracks(any())
        }
    }

    @Test
    fun `GIVEN an album without tracks, WHEN saving it, THEN the album is saved with no rows`() = runTest {
        // Given
        val noTracks = emptyList<DbTrack>()

        // When
        val result = dataSource.saveAlbum(album, noTracks)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { albumDao.upsertAlbum(album) }
        coVerify(exactly = 1) { albumDao.insertAlbumTracks(emptyList()) }
    }

    @Test
    fun `GIVEN tracks that cannot be saved, WHEN saving an album, THEN the result is an unknown error and the album is not saved`() =
        runTest {
            // Given
            coEvery { trackDao.upsertTracks(any()) } throws IllegalStateException("disk is full")

            // When
            val error = dataSource.saveAlbum(album, tracks).exceptionOrNull()

            // Then
            assertTrue(error is AppError.Unknown)
            coVerify(exactly = 0) { albumDao.upsertAlbum(any()) }
            coVerify(exactly = 0) { albumDao.insertAlbumTracks(any()) }
        }
}
