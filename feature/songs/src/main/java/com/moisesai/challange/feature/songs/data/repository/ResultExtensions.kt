package com.moisesai.challange.feature.songs.data.repository

/** Continues with another step that can fail, stopping at the first failure. Result has no such operator of its own. */
internal inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(onSuccess = transform, onFailure = { Result.failure(it) })
