package com.moisesai.challange.feature.songdetails.presentation.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.player.PlaybackState
import com.moisesai.challange.core.player.PlayerController
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.domain.model.TrackQueueDomainModel
import com.moisesai.challange.feature.songdetails.domain.usecase.GetTrackQueueUseCase
import com.moisesai.challange.feature.songdetails.domain.usecase.SaveRecentTrackUseCase
import com.moisesai.challange.core.design.extension.toUiErrorType
import com.moisesai.challange.feature.songdetails.presentation.mapper.toUiPlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiSongDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Plays the track that the route named, and the tracks of its album around it. It turns what the player reports into
 * what the screen shows, and what the user taps into calls to the player.
 */
@HiltViewModel
internal class SongDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrackQueueUseCase: GetTrackQueueUseCase,
    private val saveRecentTrackUseCase: SaveRecentTrackUseCase,
    private val playerController: PlayerController,
) : ViewModel() {

    private val chosenTrackId: Long =
        checkNotNull(savedStateHandle[TRACK_ID_KEY]) { "The player route always carries a track id." }

    private val _uiState = MutableStateFlow<UiSongDetailsUiState>(UiSongDetailsUiState.Loading)
    val uiState: StateFlow<UiSongDetailsUiState> = _uiState.asStateFlow()

    // The three things the content is built from. The queue is null until it is loaded.
    private var trackQueue: TrackQueueDomainModel? = null
    private var playbackState: PlaybackState = PlaybackState.Idle
    private var isTrackOptionsVisible = false

    init {
        observePlaybackChanges()
        loadTrackQueueAndPlay()
    }

    fun onRetry() {
        _uiState.value = UiSongDetailsUiState.Loading
        loadTrackQueueAndPlay()
    }

    fun onPlayPauseClick() {
        val isPlaying = playerController.playbackState.value is PlaybackState.Playing
        if (isPlaying) {
            playerController.pause()
        } else {
            playerController.resume()
        }
    }

    /** The user tapped or dragged the progress bar to [changedProgress]: 0 is the start of the track and 1 is its end. The bar keeps it between the two. */
    fun onTrackProgressBarChangedByUser(changedProgress: Float) {
        // Nothing is loaded, so there is nothing to jump in.
        val loadedAudio = playerController.playbackState.value as? PlaybackState.Loaded ?: return
        playerController.seekTo((loadedAudio.durationMillis * changedProgress).toLong())
    }

    fun onRepeatClick() {
        playerController.toggleRepeat()
    }

    fun goToNextTrack() {
        val queue = trackQueue ?: return
        if (queue.hasNextTrack) {
            startPlaying(queue.movedToNextTrack())
        } else {
            showContent()
        }
    }

    // The first track of the queue has no track before it, so going back restarts it.
    fun onPreviousClick() {
        val queue = trackQueue ?: return
        if (queue.hasPreviousTrack) {
            startPlaying(queue.movedToPreviousTrack())
        } else {
            playerController.seekTo(TRACK_START_MILLIS)
        }
    }

    fun onTrackOptionsClick() {
        isTrackOptionsVisible = true
        showContent()
    }

    fun onTrackOptionsDismiss() {
        isTrackOptionsVisible = false
        showContent()
    }

    // Stops the music only when it is this screen's track that is loaded. When another screen already started another
    // track, that track is not ours to stop: a screen that is closed late must not silence the one that replaced it.
    override fun onCleared() {
        val currentTrack = trackQueue?.currentTrack ?: return
        val loadedAudioUrl = (playerController.playbackState.value as? PlaybackState.PlaybackStateWithAudio)?.audioUrl
        val isCurrentTrackLoaded = loadedAudioUrl == currentTrack.previewUrl
        if (isCurrentTrackLoaded) playerController.stop()
    }

    private fun observePlaybackChanges() {
        playerController.playbackState.onEach { newPlaybackState ->
            playbackState = newPlaybackState
            when (newPlaybackState) {
                is PlaybackState.Failed -> showError(AppError.Unknown())
                // When a track ends by itself the next one starts; the last one just stays ended, ready to play again.
                is PlaybackState.Ended -> goToNextTrack()
                else -> showContent()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadTrackQueueAndPlay() {
        viewModelScope.launch {
            getTrackQueueUseCase(chosenTrackId)
                .onSuccess(::startPlaying)
                .onFailure(::showError)
        }
    }

    private fun startPlaying(queue: TrackQueueDomainModel) {
        trackQueue = queue
        val track = queue.currentTrack
        val audioUrl = track.previewUrl
        if (audioUrl == null) {
            showError(AppError.Unknown())
            return
        }
        playerController.playFromStart(audioUrl)
        showContent()
        rememberAsRecentlyPlayed(track)
    }

    // The track is already playing: not being able to remember it must not stop the music, so the result is ignored.
    private fun rememberAsRecentlyPlayed(track: TrackDomainModel) {
        viewModelScope.launch { saveRecentTrackUseCase(track) }
    }

    private fun showContent() {
        val track = trackQueue?.currentTrack ?: return
        _uiState.value = UiSongDetailsUiState.Success(
            // The names come from the domain track; the times come from the player, not from the domain.
            // This runs every 250 ms while playing, each time the player publishes a new state (see observePlaybackChanges).
            playingTrack = track.toUiPlayingTrack(playbackState),
            isTrackOptionsVisible = isTrackOptionsVisible,
        )
    }

    private fun showError(error: Throwable) {
        _uiState.value = UiSongDetailsUiState.Error(error.toUiErrorType())
    }

    private companion object {
        // The name of SongDetailsRoute's property: the navigation stores each route field in the saved state under it.
        const val TRACK_ID_KEY = "trackId"

        // Where "previous" on the first track of the queue jumps to.
        const val TRACK_START_MILLIS = 0L
    }
}
