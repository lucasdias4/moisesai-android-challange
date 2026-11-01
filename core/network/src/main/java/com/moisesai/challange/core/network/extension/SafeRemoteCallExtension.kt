package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.datacommon.safeCall

/**
 * Runs one Retrofit call and returns its outcome as a [Result], mapping HTTP errors too. Retrofit
 * suspend functions are main-safe, so there is no dispatcher switch here; the repository decides
 * where its work runs.
 *
 * It has its own error mapping, focused on API request.
 */
internal suspend fun <T> safeRemoteCall(request: suspend () -> T): Result<T> =
    safeCall(mapError = Throwable::toRemoteError, block = request)
