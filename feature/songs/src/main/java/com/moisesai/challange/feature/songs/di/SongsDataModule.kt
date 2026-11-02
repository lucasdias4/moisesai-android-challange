package com.moisesai.challange.feature.songs.di

import com.moisesai.challange.feature.songs.data.repository.RecentTracksRepositoryImpl
import com.moisesai.challange.feature.songs.data.repository.SuggestedTracksRepositoryImpl
import com.moisesai.challange.feature.songs.data.repository.TrackSearchRepositoryImpl
import com.moisesai.challange.feature.songs.domain.repository.RecentTracksRepository
import com.moisesai.challange.feature.songs.domain.repository.SuggestedTracksRepository
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SongsDataModule {

    @Binds
    abstract fun bindRecentTracksRepository(implementation: RecentTracksRepositoryImpl): RecentTracksRepository

    @Binds
    abstract fun bindTrackSearchRepository(implementation: TrackSearchRepositoryImpl): TrackSearchRepository

    @Binds
    abstract fun bindSuggestedTracksRepository(implementation: SuggestedTracksRepositoryImpl): SuggestedTracksRepository
}
