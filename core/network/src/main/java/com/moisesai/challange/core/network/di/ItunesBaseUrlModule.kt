package com.moisesai.challange.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Public so the end-to-end tests of the app can replace the address with a server of their own. */
@Module
@InstallIn(SingletonComponent::class)
object ItunesBaseUrlModule {
    private const val BASE_URL = "https://itunes.apple.com/"

    @Provides
    @ItunesBaseUrl
    fun provideItunesBaseUrl(): String = BASE_URL
}
