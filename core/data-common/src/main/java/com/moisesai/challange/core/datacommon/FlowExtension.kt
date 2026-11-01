package com.moisesai.challange.core.datacommon

import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * A Flow never throws to its collector: a failure is emitted as `Result.failure`, mapped by [mapError].
 * Cancellation is rethrown, never emitted.
 */
fun <T> Flow<T>.asSafeResult(mapError: (Throwable) -> AppError = Throwable::toAppError): Flow<Result<T>> =
    map { Result.success(it) }
        .catch {
            if (it is CancellationException) throw it
            emit(Result.failure(mapError(it)))
        }
