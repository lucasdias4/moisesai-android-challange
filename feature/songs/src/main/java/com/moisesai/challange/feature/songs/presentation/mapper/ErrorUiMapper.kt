package com.moisesai.challange.feature.songs.presentation.mapper

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

internal fun Throwable.toUiError(): UiSongsError = if (this is AppError.Network) UiSongsError.Network else UiSongsError.Unknown
