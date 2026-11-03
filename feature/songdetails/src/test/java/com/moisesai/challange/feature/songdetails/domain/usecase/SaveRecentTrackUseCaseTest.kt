package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.feature.songdetails.domain.repository.RecentTracksRepository
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveRecentTrackUseCaseTest {

    private val recentTracksRepository = mockk<RecentTracksRepository>()
    private val saveRecentTrackUseCase = SaveRecentTrackUseCase(recentTracksRepository)

    @Test
    fun `GIVEN a track that was played, WHEN saving it, THEN it is remembered as recently played`() = runTest {
        // Given
        val track = aTrackDomainModel()
        coEvery { recentTracksRepository.saveRecentTrack(track) } returns Result.success(Unit)

        // When
        val result = saveRecentTrackUseCase(track)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { recentTracksRepository.saveRecentTrack(track) }
    }
}
