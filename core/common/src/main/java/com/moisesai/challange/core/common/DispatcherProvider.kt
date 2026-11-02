package com.moisesai.challange.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Injected instead of reading [Dispatchers] directly, so tests can run on a test dispatcher. */
interface DispatcherProvider {
    /** For work that waits on a disk or a network, such as a blocking read. */
    val io: CoroutineDispatcher

    /** For work that keeps the processor busy, such as mapping a long list. */
    val default: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
