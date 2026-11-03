package com.moisesai.challange.feature.album.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.feature.album.fixture.ALBUM_ID
import com.moisesai.challange.feature.album.fixture.anAlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.fixture.anApiAlbum
import com.moisesai.challange.feature.album.fixture.anApiTrack
import com.moisesai.challange.feature.album.support.aDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlbumRepositoryImplTest {

    private val remoteDataSource = mockk<AlbumRemoteDataSource>()
    private val repository = AlbumRepositoryImpl(
        albumRemoteDataSource = remoteDataSource,
        dispatchers = aDispatcherProvider(UnconfinedTestDispatcher()),
    )

    @Test
    fun `GIVEN a lookup with an album and a track, WHEN getting the album with its tracks, THEN both are mapped to the domain`() = runTest {
        // Given
        coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns
            Result.success(ApiResponse(results = listOf(anApiAlbum(), anApiTrack())))

        // When
        val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(anAlbumWithTracksDomainModel(), albumWithTracks.getOrThrow())
    }

    @Test
    fun `GIVEN a lookup without an album, WHEN getting the album with its tracks, THEN it fails with an unknown error`() = runTest {
        // Given
        coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.success(ApiResponse(results = emptyList()))

        // When
        val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

        // Then
        assertTrue(albumWithTracks.exceptionOrNull() is AppError.Unknown)
    }

    @Test
    fun `GIVEN a lookup with an album and no tracks, WHEN getting the album with its tracks, THEN it succeeds with an empty list`() = runTest {
        // Given
        coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.success(ApiResponse(results = listOf(anApiAlbum())))

        // When
        val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(emptyList<Any>(), albumWithTracks.getOrThrow().tracks)
    }

    @Test
    fun `GIVEN a network failure, WHEN getting the album with its tracks, THEN the same failure is returned`() = runTest {
        // Given
        val networkError = AppError.Network()
        coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.failure(networkError)

        // When
        val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(networkError, albumWithTracks.exceptionOrNull())
    }
}
