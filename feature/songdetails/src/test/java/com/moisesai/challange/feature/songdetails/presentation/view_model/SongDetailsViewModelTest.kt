package com.moisesai.challange.feature.songdetails.presentation.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.player.PlaybackState
import com.moisesai.challange.core.player.PlayerController
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.domain.model.TrackQueueDomainModel
import com.moisesai.challange.feature.songdetails.domain.usecase.GetTrackQueueUseCase
import com.moisesai.challange.feature.songdetails.domain.usecase.GetTrackUseCase
import com.moisesai.challange.feature.songdetails.domain.usecase.SaveRecentTrackUseCase
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_ID
import com.moisesai.challange.feature.songdetails.fixture.LEVITATING_PREVIEW_URL
import com.moisesai.challange.feature.songdetails.fixture.aFailedState
import com.moisesai.challange.feature.songdetails.fixture.aPausedState
import com.moisesai.challange.feature.songdetails.fixture.aPlayingState
import com.moisesai.challange.feature.songdetails.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songdetails.fixture.anEndedState
import com.moisesai.challange.feature.songdetails.presentation.model.UiSongDetailsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongDetailsViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private val getTrackUseCase = mockk<GetTrackUseCase>()
    private val getTrackQueueUseCase = mockk<GetTrackQueueUseCase>()
    private val saveRecentTrackUseCase = mockk<SaveRecentTrackUseCase>()
    private val playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    private val playerController = mockk<PlayerController>(relaxed = true) {
        every { playbackState } returns this@SongDetailsViewModelTest.playbackState
    }

    private val first = aTrackDomainModel(id = LEVITATING_ID)
    private val second = aTrackDomainModel(id = 2, previewUrl = SECOND_PREVIEW_URL)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { saveRecentTrackUseCase(any()) } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN the track did not arrive yet, WHEN the screen starts, THEN it is loading`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongDetailsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `GIVEN a queue that loads, WHEN the screen starts, THEN the chosen track plays and its names are shown`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first, second)
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
        val content = viewModel.uiState.value as UiSongDetailsUiState.Success
        assertEquals(first.name, content.playingTrack.trackName)
        assertEquals(first.albumId, content.playingTrack.albumId)
    }

    @Test
    fun `GIVEN a track that fails to load, WHEN the screen starts, THEN the error is shown and nothing plays`() = runTest(dispatcher) {
        // Given
        coEvery { getTrackUseCase(LEVITATING_ID) } returns Result.failure(AppError.Network())
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        assertEquals(UiSongDetailsUiState.Error(UiErrorType.Network), viewModel.uiState.value)
        verify(exactly = 0) { playerController.playFromStart(any()) }
    }

    @Test
    fun `GIVEN a track without audio, WHEN the screen starts, THEN the error is shown and nothing plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(aTrackDomainModel(previewUrl = null))
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        assertEquals(UiSongDetailsUiState.Error(UiErrorType.Unknown), viewModel.uiState.value)
        verify(exactly = 0) { playerController.playFromStart(any()) }
    }

    @Test
    fun `GIVEN a queue that loads, WHEN the screen starts, THEN the chosen track is remembered as recently played`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        createViewModel()

        // When
        runCurrent()

        // Then
        coVerify(exactly = 1) { saveRecentTrackUseCase(first) }
    }

    @Test
    fun `GIVEN a track that cannot be remembered, WHEN the screen starts, THEN it still plays and is shown`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        coEvery { saveRecentTrackUseCase(first) } returns Result.failure(AppError.Unknown())
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
        assertTrue(viewModel.uiState.value is UiSongDetailsUiState.Success)
    }

    @Test
    fun `GIVEN the player reports progress, WHEN the screen shows the track, THEN the times follow the player`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()

        // When
        playbackState.value = aPlayingState(positionMillis = 7_000L, durationMillis = 30_000L)
        runCurrent()

        // Then
        val playingTrack = (viewModel.uiState.value as UiSongDetailsUiState.Success).playingTrack
        assertTrue(playingTrack.isPlaying)
        assertEquals("0:07", playingTrack.playedTimeText)
    }

    @Test
    fun `GIVEN a queue with a next track, WHEN the user taps next, THEN the next track plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first, second)
        val viewModel = createViewModel()
        runCurrent()

        // When
        viewModel.goToNextTrack()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(SECOND_PREVIEW_URL) }
        assertEquals(second.name, (viewModel.uiState.value as UiSongDetailsUiState.Success).playingTrack.trackName)
    }

    @Test
    fun `GIVEN the last track of the queue, WHEN the user taps next, THEN nothing new plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()

        // When
        viewModel.goToNextTrack()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(any()) }
    }

    @Test
    fun `GIVEN the second track of the queue, WHEN the user taps previous, THEN the first track plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first, second, startingAt = second)
        val viewModel = createViewModel()
        runCurrent()

        // When
        viewModel.onPreviousClick()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
    }

    @Test
    fun `GIVEN the first track of the queue, WHEN the user taps previous, THEN the track restarts`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first, second)
        val viewModel = createViewModel()
        runCurrent()

        // When
        viewModel.onPreviousClick()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.seekTo(0L) }
        verify(exactly = 1) { playerController.playFromStart(any()) }
    }

    @Test
    fun `GIVEN a track that ends and a next track, WHEN the player reports the end, THEN the next track plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first, second)
        createViewModel()
        runCurrent()

        // When
        playbackState.value = anEndedState()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(SECOND_PREVIEW_URL) }
    }

    @Test
    fun `GIVEN the last track that ends, WHEN the player reports the end, THEN no other track plays`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        createViewModel()
        runCurrent()

        // When
        playbackState.value = anEndedState()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(any()) }
    }

    @Test
    fun `GIVEN a track that is playing, WHEN the player reports that the audio failed, THEN the track stays on screen and cannot be played`() =
        runTest(dispatcher) {
            // Given
            givenQueueOf(first)
            val viewModel = createViewModel()
            runCurrent()

            // When
            playbackState.value = aFailedState()
            runCurrent()

            // Then
            val success = viewModel.uiState.value as UiSongDetailsUiState.Success
            assertEquals(first.name, success.playingTrack.trackName)
            assertFalse(success.playingTrack.canPlay)
        }

    @Test
    fun `GIVEN audio that failed, WHEN the user taps play, THEN the message that it cannot play is shown and nothing is played`() =
        runTest(dispatcher) {
            // Given
            givenQueueOf(first)
            val viewModel = createViewModel()
            runCurrent()
            playbackState.value = aFailedState()
            runCurrent()

            // When
            viewModel.onPlayPauseClick()

            // Then
            val success = viewModel.uiState.value as UiSongDetailsUiState.Success
            assertTrue(success.isCannotPlayMessageVisible)
            verify(exactly = 0) { playerController.resume() }
            verify(exactly = 0) { playerController.pause() }
        }

    @Test
    fun `GIVEN the message that it cannot play is shown, WHEN the screen reports that it showed it, THEN the message is no longer asked for`() =
        runTest(dispatcher) {
            // Given
            givenQueueOf(first)
            val viewModel = createViewModel()
            runCurrent()
            playbackState.value = aFailedState()
            runCurrent()
            viewModel.onPlayPauseClick()

            // When
            viewModel.onCannotPlayMessageShown()

            // Then
            val success = viewModel.uiState.value as UiSongDetailsUiState.Success
            assertFalse(success.isCannotPlayMessageVisible)
        }

    @Test
    fun `GIVEN a track that is playing, WHEN the user taps play and pause, THEN it pauses`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = aPlayingState()

        // When
        viewModel.onPlayPauseClick()

        // Then
        verify(exactly = 1) { playerController.pause() }
        verify(exactly = 0) { playerController.resume() }
    }

    @Test
    fun `GIVEN a track that is paused, WHEN the user taps play and pause, THEN it resumes`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = aPausedState()

        // When
        viewModel.onPlayPauseClick()

        // Then
        verify(exactly = 1) { playerController.resume() }
        verify(exactly = 0) { playerController.pause() }
    }

    @Test
    fun `GIVEN a track of 30 seconds, WHEN the user seeks to the middle, THEN the player jumps to 15 seconds`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = aPausedState(durationMillis = 30_000L)

        // When
        viewModel.onTrackProgressBarChangedByUser(0.5f)

        // Then
        verify(exactly = 1) { playerController.seekTo(15_000L) }
    }

    @Test
    fun `GIVEN nothing is loaded in the player, WHEN the user seeks, THEN the player does not jump`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = PlaybackState.Idle

        // When
        viewModel.onTrackProgressBarChangedByUser(0.5f)

        // Then
        verify(exactly = 0) { playerController.seekTo(any()) }
    }

    @Test
    fun `GIVEN the track is shown, WHEN the user taps repeat, THEN the player toggles repeat`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()

        // When
        viewModel.onRepeatClick()

        // Then
        verify(exactly = 1) { playerController.toggleRepeat() }
    }

    @Test
    fun `GIVEN the audio of the track is loaded, WHEN the screen is closed, THEN the player stops`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = aPlayingState()

        // When
        close(viewModel)

        // Then
        verify(exactly = 1) { playerController.stop() }
    }

    @Test
    fun `GIVEN the audio of another track is loaded, WHEN the screen is closed, THEN the player keeps playing`() = runTest(dispatcher) {
        // Given
        givenQueueOf(first)
        val viewModel = createViewModel()
        runCurrent()
        playbackState.value = aPlayingState().copy(audioUrl = SECOND_PREVIEW_URL)

        // When
        close(viewModel)

        // Then
        verify(exactly = 0) { playerController.stop() }
    }

    @Test
    fun `GIVEN the track is shown, WHEN the user opens and closes the options, THEN the sheet is shown and then hidden`() =
        runTest(dispatcher) {
            // Given
            givenQueueOf(first)
            val viewModel = createViewModel()
            runCurrent()

            // When
            viewModel.onTrackOptionsClick()
            val whileOpen = (viewModel.uiState.value as UiSongDetailsUiState.Success).isTrackOptionsVisible
            viewModel.onTrackOptionsDismiss()
            val afterClosing = (viewModel.uiState.value as UiSongDetailsUiState.Success).isTrackOptionsVisible

            // Then
            assertTrue(whileOpen)
            assertFalse(afterClosing)
        }

    @Test
    fun `GIVEN the album is still loading, WHEN the chosen track arrives, THEN it plays and is shown without waiting for the album`() =
        runTest(dispatcher) {
            // Given
            coEvery { getTrackUseCase(LEVITATING_ID) } returns Result.success(first)
            coEvery { getTrackQueueUseCase(LEVITATING_ID) } coAnswers { awaitCancellation() }
            val viewModel = createViewModel()

            // When
            runCurrent()

            // Then
            verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
            assertTrue(viewModel.uiState.value is UiSongDetailsUiState.Success)
        }

    @Test
    fun `GIVEN the chosen track is playing, WHEN the album arrives, THEN the music is not restarted and the next track is there`() =
        runTest(dispatcher) {
            // Given
            givenQueueOf(first, second)
            val viewModel = createViewModel()

            // When
            runCurrent()
            viewModel.goToNextTrack()

            // Then
            verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
            verify(exactly = 1) { playerController.playFromStart(SECOND_PREVIEW_URL) }
        }

    @Test
    fun `GIVEN the album fails to load, WHEN the chosen track plays, THEN it keeps playing alone`() = runTest(dispatcher) {
        // Given
        coEvery { getTrackUseCase(LEVITATING_ID) } returns Result.success(first)
        coEvery { getTrackQueueUseCase(LEVITATING_ID) } returns Result.failure(AppError.Network())
        val viewModel = createViewModel()

        // When
        runCurrent()

        // Then
        assertTrue(viewModel.uiState.value is UiSongDetailsUiState.Success)
        verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
    }

    @Test
    fun `GIVEN the next track has no audio, WHEN the user taps next, THEN the error is shown and nothing more plays`() =
        runTest(dispatcher) {
            // Given
            val withoutAudio = aTrackDomainModel(id = 3, previewUrl = null)
            givenQueueOf(first, withoutAudio)
            val viewModel = createViewModel()
            runCurrent()

            // When
            viewModel.goToNextTrack()

            // Then
            assertEquals(UiSongDetailsUiState.Error(UiErrorType.Unknown), viewModel.uiState.value)
            verify(exactly = 1) { playerController.playFromStart(any()) }
        }

    @Test
    fun `GIVEN the track did not arrive yet, WHEN the screen is closed, THEN the player is not stopped`() = runTest(dispatcher) {
        // Given
        coEvery { getTrackUseCase(LEVITATING_ID) } coAnswers { awaitCancellation() }
        val viewModel = createViewModel()
        runCurrent()

        // When
        close(viewModel)

        // Then
        verify(exactly = 0) { playerController.stop() }
    }

    @Test
    fun `GIVEN the track failed to load, WHEN the user retries and it loads, THEN the chosen track plays`() = runTest(dispatcher) {
        // Given
        coEvery { getTrackUseCase(LEVITATING_ID) } returns Result.failure(AppError.Network())
        val viewModel = createViewModel()
        runCurrent()
        givenQueueOf(first)

        // When
        viewModel.onRetry()
        runCurrent()

        // Then
        verify(exactly = 1) { playerController.playFromStart(LEVITATING_PREVIEW_URL) }
        assertTrue(viewModel.uiState.value is UiSongDetailsUiState.Success)
    }

    private fun givenQueueOf(vararg tracks: TrackDomainModel, startingAt: TrackDomainModel = tracks.first()) {
        coEvery { getTrackUseCase(LEVITATING_ID) } returns Result.success(startingAt)
        coEvery { getTrackQueueUseCase(LEVITATING_ID) } returns
            Result.success(TrackQueueDomainModel(tracks.toList(), currentIndex = tracks.indexOf(startingAt)))
    }

    // Clearing the store that holds the ViewModel is what the framework does when the screen goes away: it calls onCleared.
    @Suppress("UNCHECKED_CAST")
    private fun close(viewModel: SongDetailsViewModel) {
        val store = ViewModelStore()
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
        }
        ViewModelProvider(store, factory)[SongDetailsViewModel::class.java]
        store.clear()
    }

    private fun createViewModel() = SongDetailsViewModel(
        savedStateHandle = SavedStateHandle(mapOf("trackId" to LEVITATING_ID)),
        getTrackUseCase = getTrackUseCase,
        getTrackQueueUseCase = getTrackQueueUseCase,
        saveRecentTrackUseCase = saveRecentTrackUseCase,
        playerController = playerController,
    )

    private companion object {
        const val SECOND_PREVIEW_URL = "https://audio.example/second-track.m4a"
    }
}
