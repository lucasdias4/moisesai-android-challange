package com.moisesai.challange.feature.album.presentation.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moisesai.challange.core.design.extension.toUiErrorType
import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.domain.usecase.GetAlbumWithTracksUseCase
import com.moisesai.challange.feature.album.presentation.mapper.toUiModel
import com.moisesai.challange.feature.album.presentation.model.UiAlbumUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads the album that the route named, and loads it again when the user asks to try again. */
@HiltViewModel
internal class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumWithTracksUseCase: GetAlbumWithTracksUseCase,
) : ViewModel() {

    private val albumId: Long =
        checkNotNull(savedStateHandle[ALBUM_ID_KEY]) { "The album route always carries an album id." }

    private val _uiState = MutableStateFlow<UiAlbumUiState>(UiAlbumUiState.Loading)
    val uiState: StateFlow<UiAlbumUiState> = _uiState.asStateFlow()

    init {
        loadAlbum()
    }

    fun onRetry() {
        _uiState.value = UiAlbumUiState.Loading
        loadAlbum()
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            getAlbumWithTracksUseCase(albumId)
                .onSuccess { albumWithTracks ->
                    _uiState.value = UiAlbumUiState.Success(albumWithTracks.toUiModel())
                }
                .onFailure { error ->
                    _uiState.value = UiAlbumUiState.Error(error.toUiErrorType())
                }
        }
    }
    private companion object {
        const val ALBUM_ID_KEY = "albumId"
    }
}
