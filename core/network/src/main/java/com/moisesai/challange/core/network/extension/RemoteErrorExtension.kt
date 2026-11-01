package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.common.toAppError
import retrofit2.HttpException

// toAppError rethrows CancellationException, so cancellation never becomes a failure.
internal fun Throwable.toRemoteError(): AppError =
    if (this is HttpException) AppError.Network(this) else toAppError()
