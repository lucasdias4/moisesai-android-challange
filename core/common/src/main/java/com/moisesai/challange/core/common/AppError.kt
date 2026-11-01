package com.moisesai.challange.core.common

/**
 * Extends [Exception] because layers propagate failures as `Result<T>`, whose failure side is a [Throwable].
 * There is no "empty" error: an empty result is a successful, empty list.
 */
sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : AppError("Network error", cause)
    class Unknown(cause: Throwable? = null) : AppError("Unknown error", cause)
}
