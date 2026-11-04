package com.moisesai.challange.e2e.support

import com.moisesai.challange.core.network.di.ItunesBaseUrl
import com.moisesai.challange.core.network.di.ItunesBaseUrlModule
import com.moisesai.challange.core.player.PlayerController
import com.moisesai.challange.core.player.di.PlayerModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/** The app talks to the [FakeBackend] instead of iTunes. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ItunesBaseUrlModule::class])
object FakeBackendModule {

    @Provides
    @ItunesBaseUrl
    fun provideFakeBackendUrl(): String = FakeBackend.baseUrl
}

/** The app plays on the [FakePlayerController], which makes no sound. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [PlayerModule::class])
abstract class FakePlayerModule {

    @Binds
    abstract fun bindFakePlayer(implementation: FakePlayerController): PlayerController
}
