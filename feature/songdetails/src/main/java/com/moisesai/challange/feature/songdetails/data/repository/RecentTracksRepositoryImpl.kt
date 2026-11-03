package com.moisesai.challange.feature.songdetails.data.repository

import com.moisesai.challange.core.common.TimeProvider
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.data.mapper.toDb
import com.moisesai.challange.feature.songdetails.domain.repository.RecentTracksRepository
import javax.inject.Inject

internal class RecentTracksRepositoryImpl @Inject constructor(
    private val recentlyPlayedTrackLocalDataSource: RecentlyPlayedTrackLocalDataSource,
    // Injected to facilitate unit testing
    private val timeProvider: TimeProvider,
) : RecentTracksRepository {

    override suspend fun saveRecentTrack(track: TrackDomainModel): Result<Unit> =
        recentlyPlayedTrackLocalDataSource.saveRecentlyPlayedTrack(track.toDb(), timeProvider.currentTimeMillis())
}
