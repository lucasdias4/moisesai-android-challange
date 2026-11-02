package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.TrackLocalDataSource
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.feature.songs.fixture.aDbTrack
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.fixture.anApiTrack
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SuggestedTracksRepositoryImplTest {

    private val localDataSource = mockk<TrackLocalDataSource>()
    private val remoteDataSource = mockk<TrackRemoteDataSource>()
    private val repository = SuggestedTracksRepositoryImpl(localDataSource, remoteDataSource)

    @Test
    fun `GIVEN every track is saved, WHEN getting the suggestions, THEN they come from the database without a request`() = runTest {
        // Given
        coEvery { localDataSource.getTrack(1L) } returns Result.success(aDbTrack(id = 1))
        coEvery { localDataSource.getTrack(2L) } returns Result.success(aDbTrack(id = 2))

        // When
        val tracks = repository.getSuggestedTracks(listOf(1L, 2L)).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel(id = 1), aTrackDomainModel(id = 2)), tracks)
        coVerify(exactly = 0) { remoteDataSource.getTrack(any()) }
    }

    @Test
    fun `GIVEN a track is not saved, WHEN getting the suggestions, THEN it is fetched from iTunes and saved`() = runTest {
        // Given
        coEvery { localDataSource.getTrack(1L) } returns Result.success(null)
        coEvery { remoteDataSource.getTrack(1L) } returns Result.success(ApiResponse(listOf(anApiTrack(id = 1))))
        coEvery { localDataSource.saveTrack(any()) } returns Result.success(Unit)

        // When
        val tracks = repository.getSuggestedTracks(listOf(1L)).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel(id = 1)), tracks)
        coVerify { localDataSource.saveTrack(aDbTrack(id = 1)) }
    }

    @Test
    fun `GIVEN tracks resolved in a different order, WHEN getting the suggestions, THEN they keep the order of the ids`() = runTest {
        // Given
        coEvery { localDataSource.getTrack(3L) } returns Result.success(aDbTrack(id = 3))
        coEvery { localDataSource.getTrack(1L) } returns Result.success(null)
        coEvery { remoteDataSource.getTrack(1L) } returns Result.success(ApiResponse(listOf(anApiTrack(id = 1))))
        coEvery { localDataSource.saveTrack(any()) } returns Result.success(Unit)

        // When
        val tracks = repository.getSuggestedTracks(listOf(3L, 1L)).getOrThrow()

        // Then
        assertEquals(listOf(3L, 1L), tracks.map { it.id })
    }

    @Test
    fun `GIVEN one track fails and the others work, WHEN getting the suggestions, THEN the working ones are returned`() = runTest {
        // Given
        coEvery { localDataSource.getTrack(1L) } returns Result.success(aDbTrack(id = 1))
        coEvery { localDataSource.getTrack(2L) } returns Result.success(null)
        coEvery { remoteDataSource.getTrack(2L) } returns Result.failure(AppError.Network())

        // When
        val tracks = repository.getSuggestedTracks(listOf(1L, 2L)).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel(id = 1)), tracks)
    }

    @Test
    fun `GIVEN every track fails, WHEN getting the suggestions, THEN the failure is returned`() = runTest {
        // Given
        val error = AppError.Network()
        coEvery { localDataSource.getTrack(any()) } returns Result.success(null)
        coEvery { remoteDataSource.getTrack(any()) } returns Result.failure(error)

        // When
        val result = repository.getSuggestedTracks(listOf(1L, 2L))

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }

    @Test
    fun `GIVEN iTunes does not know a track, WHEN getting the suggestions, THEN it is left out without an error`() = runTest {
        // Given
        coEvery { localDataSource.getTrack(1L) } returns Result.success(null)
        coEvery { remoteDataSource.getTrack(1L) } returns Result.success(ApiResponse(emptyList()))

        // When
        val result = repository.getSuggestedTracks(listOf(1L))

        // Then
        assertEquals(emptyList<Any>(), result.getOrThrow())
    }
}
