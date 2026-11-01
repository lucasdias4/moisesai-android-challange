package com.moisesai.challange.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Injected instead of reading [Dispatchers] directly, so tests can run on a test dispatcher. */
interface DispatcherProvider {
    val io: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
}
