package com.moisesai.challange.feature.songdetails.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.TrackLocalDataSource
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ALBUM_ID
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ID
import com.moisesai.challange.feature.songdetails.fixture.aDbTrack
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songdetails.fixture.anApiTrack
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackRepositoryImplTest {

    private val trackLocalDataSource = mockk<TrackLocalDataSource>()
    private val trackRemoteDataSource = mockk<TrackRemoteDataSource>()
    private val albumRemoteDataSource = mockk<AlbumRemoteDataSource>()
    private val repository = TrackRepositoryImpl(trackLocalDataSource, trackRemoteDataSource, albumRemoteDataSource)

    @Test
    fun `GIVEN a saved track, WHEN getting it, THEN the saved track is returned without a request`() = runTest {
        // Given
        coEvery { trackLocalDataSource.getTrack(LEVITATING_ID) } returns Result.success(aDbTrack())

        // When
        val track = repository.getTrack(LEVITATING_ID)

        // Then
        assertEquals(aTrackDomainModel(), track.getOrThrow())
        coVerify(exactly = 0) { trackRemoteDataSource.getTrack(any()) }
    }

    @Test
    fun `GIVEN a track that is not saved, WHEN getting it, THEN it is fetched and saved`() = runTest {
        // Given
        coEvery { trackLocalDataSource.getTrack(LEVITATING_ID) } returns Result.success(null)
        coEvery { trackRemoteDataSource.getTrack(LEVITATING_ID) } returns Result.success(ApiResponse(listOf(anApiTrack())))
        coEvery { trackLocalDataSource.saveTrack(any()) } returns Result.success(Unit)

        // When
        val track = repository.getTrack(LEVITATING_ID)

        // Then
        assertEquals(aTrackDomainModel(), track.getOrThrow())
        coVerify(exactly = 1) { trackLocalDataSource.saveTrack(aDbTrack()) }
    }

    @Test
    fun `GIVEN a track that is not saved and a lookup that finds nothing, WHEN getting it, THEN it fails with an unknown error`() =
        runTest {
            // Given
            coEvery { trackLocalDataSource.getTrack(LEVITATING_ID) } returns Result.success(null)
            coEvery { trackRemoteDataSource.getTrack(LEVITATING_ID) } returns Result.success(ApiResponse(emptyList()))

            // When
            val track = repository.getTrack(LEVITATING_ID)

            // Then
            assertTrue(track.exceptionOrNull() is AppError.Unknown)
        }

    @Test
    fun `GIVEN a track that is not saved and no connection, WHEN getting it, THEN it fails with the network error`() = runTest {
        // Given
        val networkError = AppError.Network()
        coEvery { trackLocalDataSource.getTrack(LEVITATING_ID) } returns Result.success(null)
        coEvery { trackRemoteDataSource.getTrack(LEVITATING_ID) } returns Result.failure(networkError)

        // When
        val track = repository.getTrack(LEVITATING_ID)

        // Then
        assertEquals(networkError, track.exceptionOrNull())
    }

    @Test
    fun `GIVEN an album lookup with a track, WHEN getting the tracks of the album, THEN they are mapped to the domain`() = runTest {
        // Given
        coEvery { albumRemoteDataSource.getAlbumWithTracks(LEVITATING_ALBUM_ID) } returns Result.success(ApiResponse(listOf(anApiTrack())))

        // When
        val tracks = repository.getAlbumTracks(LEVITATING_ALBUM_ID)

        // Then
        assertEquals(listOf(aTrackDomainModel()), tracks.getOrThrow())
    }
}
