package com.moisesai.challange.feature.album.di

import com.moisesai.challange.feature.album.data.repository.AlbumRepositoryImpl
import com.moisesai.challange.feature.album.domain.repository.AlbumRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AlbumDataModule {

    @Binds
    abstract fun bindAlbumRepository(implementation: AlbumRepositoryImpl): AlbumRepository
}
