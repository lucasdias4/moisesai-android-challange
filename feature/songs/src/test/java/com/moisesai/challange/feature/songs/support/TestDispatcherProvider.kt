package com.moisesai.challange.feature.songs.support

import com.moisesai.challange.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

/** A [DispatcherProvider] whose io dispatcher is the one the test gives, so tests control when work runs. */
fun aDispatcherProvider(dispatcher: CoroutineDispatcher) = object : DispatcherProvider {
    override val io: CoroutineDispatcher = dispatcher
}
