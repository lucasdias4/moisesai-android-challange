package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.support.aDbTrack
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackLocalDataSourceTest {

    private val trackDao = mockk<TrackDao>(relaxed = true)
    private val dataSource = TrackLocalDataSource(trackDao)

    @Test
    fun `GIVEN a cached track, WHEN getting it, THEN the result is a success with the track`() = runTest {
        // Given
        val track = aDbTrack(id = 7)
        coEvery { trackDao.getTrack(7) } returns track

        // When
        val result = dataSource.getTrack(trackId = 7)

        // Then
        assertEquals(track, result.getOrThrow())
    }

    @Test
    fun `GIVEN a track that is not cached, WHEN getting it, THEN the result is a success with no track`() = runTest {
        // Given
        coEvery { trackDao.getTrack(7) } returns null

        // When
        val result = dataSource.getTrack(trackId = 7)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `GIVEN a database that fails, WHEN getting a track, THEN the result is an unknown error`() = runTest {
        // Given
        coEvery { trackDao.getTrack(any()) } throws IllegalStateException("database is closed")

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN a track, WHEN saving it, THEN the track is upserted and the result is a success`() = runTest {
        // Given
        val track = aDbTrack(id = 7)

        // When
        val result = dataSource.saveTrack(track)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { trackDao.upsertTrack(track) }
    }

    @Test
    fun `GIVEN a database that fails, WHEN saving a track, THEN the result is an unknown error`() = runTest {
        // Given
        coEvery { trackDao.upsertTrack(any()) } throws IllegalStateException("database is closed")

        // When
        val error = dataSource.saveTrack(aDbTrack()).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }
}
