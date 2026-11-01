package com.moisesai.challange.core.database.extension

import com.moisesai.challange.core.common.toAppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** A Flow never throws to its collector: a failure is emitted as `Result.failure`. */
internal fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    map { Result.success(it) }
        // toAppError rethrows CancellationException, so cancelling the collector is never swallowed.
        .catch { emit(Result.failure(it.toAppError())) }
