package com.moisesai.challange.core.network.di

import com.moisesai.challange.core.network.api.ItunesApi
import com.moisesai.challange.core.network.api.ItunesJson
import com.moisesai.challange.core.network.api.createItunesApi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkProvidesModule {
    private const val TIMEOUT_SECONDS = 15L

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideItunesApi(client: Lazy<OkHttpClient>, @ItunesBaseUrl baseUrl: String): ItunesApi =
        createItunesApi(baseUrl, client::get, ItunesJson)
}
