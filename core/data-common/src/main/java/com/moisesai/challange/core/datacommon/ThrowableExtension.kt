package com.moisesai.challange.core.datacommon

import com.moisesai.challange.core.common.AppError
import java.io.IOException

fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is IOException -> AppError.Network(this)
    else -> AppError.Unknown(this)
}
