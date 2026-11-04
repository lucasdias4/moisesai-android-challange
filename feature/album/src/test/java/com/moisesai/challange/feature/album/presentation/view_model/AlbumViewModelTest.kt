package com.moisesai.challange.feature.album.presentation.view_model

import androidx.lifecycle.SavedStateHandle
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.feature.album.domain.usecase.GetAlbumWithTracksUseCase
import com.moisesai.challange.feature.album.fixture.ALBUM_ID
import com.moisesai.challange.feature.album.fixture.anAlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.presentation.mapper.toUiModel
import com.moisesai.challange.feature.album.presentation.model.UiAlbumUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private val getAlbumWithTracksUseCase = mockk<GetAlbumWithTracksUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN the album did not arrive yet, WHEN the screen starts, THEN it is loading`() = runTest(dispatcher) {
        // Given
        coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.success(anAlbumWithTracksDomainModel())

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiAlbumUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `GIVEN an album that loads, WHEN the screen starts, THEN the album is shown`() = runTest(dispatcher) {
        // Given
        coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.success(anAlbumWithTracksDomainModel())
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        assertEquals(UiAlbumUiState.Success(anAlbumWithTracksDomainModel().toUiModel()), viewModel.uiState.value)
    }

    @Test
    fun `GIVEN an album with no tracks, WHEN the screen starts, THEN the album is shown with an empty list and no error`() =
        runTest(dispatcher) {
            // Given
            val albumWithoutTracks = anAlbumWithTracksDomainModel().copy(tracks = emptyList())
            coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.success(albumWithoutTracks)
            val viewModel = createViewModel()

            // When
            runCurrent()

            // Then
            assertEquals(UiAlbumUiState.Success(albumWithoutTracks.toUiModel()), viewModel.uiState.value)
        }

    @Test
    fun `GIVEN an album that fails to load, WHEN the screen starts, THEN the error is shown`() = runTest(dispatcher) {
        // Given
        coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.failure(AppError.Network())
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        assertEquals(UiAlbumUiState.Error(UiErrorType.Network), viewModel.uiState.value)
    }

    @Test
    fun `GIVEN the album failed to load, WHEN the user retries and it loads, THEN the album is shown`() = runTest(dispatcher) {
        // Given
        coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.failure(AppError.Network())
        val viewModel = createViewModel()
        runCurrent()
        coEvery { getAlbumWithTracksUseCase(ALBUM_ID) } returns Result.success(anAlbumWithTracksDomainModel())

        // When
        viewModel.onRetry()
        runCurrent()

        // Then
        assertEquals(UiAlbumUiState.Success(anAlbumWithTracksDomainModel().toUiModel()), viewModel.uiState.value)
        coVerify(exactly = 2) { getAlbumWithTracksUseCase(ALBUM_ID) }
    }

    private fun createViewModel() = AlbumViewModel(
        savedStateHandle = SavedStateHandle(mapOf("albumId" to ALBUM_ID)),
        getAlbumWithTracksUseCase = getAlbumWithTracksUseCase,
    )
}
