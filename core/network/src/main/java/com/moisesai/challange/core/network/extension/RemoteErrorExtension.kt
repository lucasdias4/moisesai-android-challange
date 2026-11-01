package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.datacommon.toAppError
import retrofit2.HttpException

/** The general mapping plus the one rule only Retrofit knows: a non-2xx HTTP status is a network error. */
internal fun Throwable.toRemoteError(): AppError =
    if (this is HttpException) AppError.Network(this) else toAppError()
