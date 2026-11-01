package com.moisesai.challange.di

import com.moisesai.challange.core.common.DefaultDispatcherProvider
import com.moisesai.challange.core.common.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object CommonModule {
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider
}
