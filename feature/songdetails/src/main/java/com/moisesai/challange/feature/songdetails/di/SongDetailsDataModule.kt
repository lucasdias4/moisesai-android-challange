package com.moisesai.challange.feature.songdetails.di

import com.moisesai.challange.feature.songdetails.data.repository.RecentTracksRepositoryImpl
import com.moisesai.challange.feature.songdetails.data.repository.TrackRepositoryImpl
import com.moisesai.challange.feature.songdetails.domain.repository.RecentTracksRepository
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SongDetailsDataModule {

    @Binds
    abstract fun bindTrackRepository(implementation: TrackRepositoryImpl): TrackRepository

    @Binds
    abstract fun bindRecentTracksRepository(implementation: RecentTracksRepositoryImpl): RecentTracksRepository
}
