package com.moisesai.challange.core.network.extension

/**
 * Runs one Retrofit call and returns its outcome as a [Result]. Retrofit suspend functions are
 * main-safe, so there is no dispatcher switch here; the repository decides where its work runs.
 */
internal suspend fun <T> remoteCall(request: suspend () -> T): Result<T> =
    runCatching { request() }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it.toRemoteError()) },
    )
