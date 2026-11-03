package com.moisesai.challange.feature.album.domain.usecase

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.album.domain.repository.AlbumRepository
import com.moisesai.challange.feature.album.fixture.ALBUM_ID
import com.moisesai.challange.feature.album.fixture.anAlbumWithTracksDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAlbumWithTracksUseCaseTest {

    private val albumRepository = mockk<AlbumRepository>()
    private val getAlbumWithTracks = GetAlbumWithTracksUseCase(albumRepository)

    @Test
    fun `GIVEN an album that the repository finds, WHEN getting the album with its tracks, THEN the album is returned`() = runTest {
        // Given
        coEvery { albumRepository.getAlbumWithTracks(ALBUM_ID) } returns Result.success(anAlbumWithTracksDomainModel())

        // When
        val albumWithTracks = getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(anAlbumWithTracksDomainModel(), albumWithTracks.getOrThrow())
    }

    @Test
    fun `GIVEN a repository that fails, WHEN getting the album with its tracks, THEN the same failure is returned`() = runTest {
        // Given
        val networkError = AppError.Network()
        coEvery { albumRepository.getAlbumWithTracks(ALBUM_ID) } returns Result.failure(networkError)

        // When
        val albumWithTracks = getAlbumWithTracks(ALBUM_ID)

        // Then
        assertEquals(networkError, albumWithTracks.exceptionOrNull())
    }
}
