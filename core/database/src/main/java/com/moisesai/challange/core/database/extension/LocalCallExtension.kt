package com.moisesai.challange.core.database.extension

import com.moisesai.challange.core.common.toAppError

/**
 * Runs one database call and returns its outcome as a [Result]. Room suspend functions are
 * main-safe, so there is no dispatcher switch here; the repository decides where its work runs.
 */
internal suspend fun <T> localCall(request: suspend () -> T): Result<T> =
    runCatching { request() }.fold(
        onSuccess = { Result.success(it) },
        // toAppError rethrows CancellationException, so cancellation never becomes a failure.
        onFailure = { Result.failure(it.toAppError()) },
    )
