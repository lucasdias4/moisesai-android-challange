package com.moisesai.challange.feature.album.presentation.model

import com.moisesai.challange.core.design.model.UiErrorType

/** What the album screen shows: the album is on its way, it failed to come, or it is here. */
internal sealed interface UiAlbumUiState {

    data object Loading : UiAlbumUiState

    data class Error(val type: UiErrorType) : UiAlbumUiState

    data class Success(val album: UiAlbum) : UiAlbumUiState
}
