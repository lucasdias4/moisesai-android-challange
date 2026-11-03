package com.moisesai.challange.feature.songdetails.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.TrackLocalDataSource
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.getOnlyTracks
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.data.mapper.toDb
import com.moisesai.challange.feature.songdetails.data.mapper.toDomain
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import javax.inject.Inject

internal class TrackRepositoryImpl @Inject constructor(
    private val trackLocalDataSource: TrackLocalDataSource,
    private val trackRemoteDataSource: TrackRemoteDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
) : TrackRepository {

    override suspend fun getTrack(trackId: Long): Result<TrackDomainModel> {
        val savedTrack = trackLocalDataSource.getTrack(trackId).getOrElse {
            return Result.failure(it)
        }
        if (savedTrack != null) return Result.success(savedTrack.toDomain())
        return getTrackFromApiAndSaveToDatabase(trackId)
    }

    override suspend fun getAlbumTracks(albumId: Long): Result<List<TrackDomainModel>> =
        albumRemoteDataSource.getAlbumWithTracks(albumId).map { response ->
            response.getOnlyTracks().map(ApiTrack::toDomain)
        }

    private suspend fun getTrackFromApiAndSaveToDatabase(trackId: Long): Result<TrackDomainModel> {
        val response =
            trackRemoteDataSource.getTrack(trackId).getOrElse { return Result.failure(it) }
        val apiTrack =
            response.getOnlyTracks().firstOrNull() ?: return Result.failure(AppError.Unknown())
        trackLocalDataSource.saveTrack(apiTrack.toDb())
        return Result.success(apiTrack.toDomain())
    }
}
