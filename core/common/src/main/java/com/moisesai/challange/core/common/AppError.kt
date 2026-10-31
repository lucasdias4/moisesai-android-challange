package com.moisesai.challange.core.common

/**
 * Extends [Exception] because layers propagate failures as `Result<T>`, whose failure side is a [Throwable].
 */
sealed class AppError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : AppError("Network error", cause)
    class Empty : AppError("No results")
    class Unknown(cause: Throwable? = null) : AppError("Unknown error", cause)
}
