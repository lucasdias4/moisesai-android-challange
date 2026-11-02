package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.common.DispatcherProvider
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.data.mapper.toDomain
import com.moisesai.challange.feature.songs.domain.repository.RecentTracksRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class RecentTracksRepositoryImpl @Inject constructor(
    private val recentlyPlayedTrackLocalDataSource: RecentlyPlayedTrackLocalDataSource,
    private val dispatchers: DispatcherProvider,
) : RecentTracksRepository {

    override fun observeRecentTracks(limit: Int): Flow<Result<List<TrackDomainModel>>> =
        recentlyPlayedTrackLocalDataSource.observeRecentlyPlayedTracks(limit)
            .map { result -> result.map { tracks -> tracks.map(DbTrack::toDomain) } }
            .flowOn(dispatchers.io)
}
