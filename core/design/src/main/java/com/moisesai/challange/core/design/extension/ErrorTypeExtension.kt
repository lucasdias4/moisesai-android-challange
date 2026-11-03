package com.moisesai.challange.core.design.extension

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.design.model.UiErrorType

/** The kind of failure the user is told about: a network failure, or anything else. */
fun Throwable.toUiErrorType(): UiErrorType =
    if (this is AppError.Network) UiErrorType.Network else UiErrorType.Unknown
