package com.moisesai.challange.core.datacommon

import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs [block] and returns its outcome as a [Result]; a failure is turned into an [AppError] by [mapError].
 * It never throws, except for cancellation.
 * Cancellation is rethrown here, for every mapper: turning it into a failure would stop the coroutine from being cancelled.
 */
suspend fun <T> safeCall(
    mapError: (Throwable) -> AppError = Throwable::toAppError,
    block: suspend () -> T,
): Result<T> =
    runCatching { block() }.fold(
        onSuccess = { Result.success(it) },
        onFailure = {
            if (it is CancellationException) throw it
            Result.failure(mapError(it))
        },
    )
