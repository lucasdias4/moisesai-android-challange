package com.moisesai.challange.feature.album.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.AlbumLocalDataSource
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.feature.album.fixture.ALBUM_ID
import com.moisesai.challange.feature.album.fixture.aDbAlbum
import com.moisesai.challange.feature.album.fixture.aDbTrack
import com.moisesai.challange.feature.album.fixture.anAlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.fixture.anApiAlbum
import com.moisesai.challange.feature.album.fixture.anApiTrack
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlbumRepositoryImplTest {

    private val remoteDataSource = mockk<AlbumRemoteDataSource>()
    private val localDataSource = mockk<AlbumLocalDataSource>()
    private val repository = AlbumRepositoryImpl(
        albumRemoteDataSource = remoteDataSource,
        albumLocalDataSource = localDataSource,
    )

    @Test
    fun `GIVEN an album in the cache, WHEN getting the album with its tracks, THEN it comes from the cache and the network is not used`() =
        runTest {
            // Given
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returns Result.success(aDbAlbum())
            coEvery { localDataSource.getAlbumTracks(ALBUM_ID) } returns Result.success(listOf(aDbTrack()))

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertEquals(anAlbumWithTracksDomainModel(), albumWithTracks.getOrThrow())
            coVerify(exactly = 0) { remoteDataSource.getAlbumWithTracks(any()) }
        }

    @Test
    fun `GIVEN an album that is not in the cache, WHEN getting the album with its tracks, THEN it is fetched, saved and returned`() =
        runTest {
            // Given
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returnsMany listOf(Result.success(null), Result.success(aDbAlbum()))
            coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns
                Result.success(ApiResponse(results = listOf(anApiAlbum(), anApiTrack())))
            coEvery { localDataSource.saveAlbum(any(), any()) } returns Result.success(Unit)
            coEvery { localDataSource.getAlbumTracks(ALBUM_ID) } returns Result.success(listOf(aDbTrack()))

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertEquals(anAlbumWithTracksDomainModel(), albumWithTracks.getOrThrow())
            coVerify(exactly = 1) { localDataSource.saveAlbum(aDbAlbum(), listOf(aDbTrack())) }
        }

    @Test
    fun `GIVEN a lookup without an album, WHEN getting the album with its tracks, THEN it fails with an unknown error and saves nothing`() =
        runTest {
            // Given
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returns Result.success(null)
            coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.success(ApiResponse(results = emptyList()))

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertTrue(albumWithTracks.exceptionOrNull() is AppError.Unknown)
            coVerify(exactly = 0) { localDataSource.saveAlbum(any(), any()) }
        }

    @Test
    fun `GIVEN a lookup with an album and no tracks, WHEN getting the album with its tracks, THEN it saves and succeeds with an empty list`() =
        runTest {
            // Given
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returnsMany listOf(Result.success(null), Result.success(aDbAlbum()))
            coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.success(ApiResponse(results = listOf(anApiAlbum())))
            coEvery { localDataSource.saveAlbum(any(), any()) } returns Result.success(Unit)
            coEvery { localDataSource.getAlbumTracks(ALBUM_ID) } returns Result.success(emptyList())

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertEquals(emptyList<Any>(), albumWithTracks.getOrThrow().tracks)
            coVerify(exactly = 1) { localDataSource.saveAlbum(aDbAlbum(), emptyList()) }
        }

    @Test
    fun `GIVEN an album that is not in the cache and a network failure, WHEN getting the album with its tracks, THEN the same failure is returned and nothing is saved`() =
        runTest {
            // Given
            val networkError = AppError.Network()
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returns Result.success(null)
            coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns Result.failure(networkError)

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertEquals(networkError, albumWithTracks.exceptionOrNull())
            coVerify(exactly = 0) { localDataSource.saveAlbum(any(), any()) }
        }

    @Test
    fun `GIVEN an album that cannot be saved, WHEN getting the album with its tracks, THEN the save failure is returned`() = runTest {
        // Given
        val saveError = AppError.Unknown()
        coEvery { localDataSource.getAlbum(ALBUM_ID) } returns Result.success(null)
        coEvery { remoteDataSource.getAlbumWithTracks(ALBUM_ID) } returns
            Result.success(ApiResponse(results = listOf(anApiAlbum(), anApiTrack())))
        coEvery { localDataSource.saveAlbum(any(), any()) } returns Result.failure(saveError)

        // When
        val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(saveError, albumWithTracks.exceptionOrNull())
    }

    @Test
    fun `GIVEN the cache cannot be read, WHEN getting the album with its tracks, THEN the read failure is returned and the network is not used`() =
        runTest {
            // Given
            val readError = AppError.Unknown()
            coEvery { localDataSource.getAlbum(ALBUM_ID) } returns Result.failure(readError)

            // When
            val albumWithTracks = repository.getAlbumWithTracks(ALBUM_ID)

            // Then
            assertEquals(readError, albumWithTracks.exceptionOrNull())
            coVerify(exactly = 0) { remoteDataSource.getAlbumWithTracks(any()) }
        }
}
