package com.moisesai.challange.core.common

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Cancellation is rethrown, never converted: turning it into a failure would stop the
 * coroutine from being cancelled.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is CancellationException -> throw this
    is IOException -> AppError.Network(this)
    else -> AppError.Unknown(this)
}
