package com.moisesai.challange.feature.album.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.datasource.AlbumLocalDataSource
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.extension.getOnlyAlbum
import com.moisesai.challange.core.network.extension.getOnlyTracks
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.feature.album.data.mapper.toDb
import com.moisesai.challange.feature.album.data.mapper.toDomain
import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.domain.repository.AlbumRepository
import javax.inject.Inject

internal class AlbumRepositoryImpl @Inject constructor(
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val albumLocalDataSource: AlbumLocalDataSource,
) : AlbumRepository {

    override suspend fun getAlbumWithTracks(albumId: Long): Result<AlbumWithTracksDomainModel> {
        saveAlbumFromApiIfNotCached(albumId).onFailure { return Result.failure(it) }
        return getAlbumWithTracksFromCache(albumId)
    }

    private suspend fun saveAlbumFromApiIfNotCached(albumId: Long): Result<Unit> {
        val cachedAlbum = albumLocalDataSource.getAlbum(albumId).getOrElse { return Result.failure(it) }
        val isAlbumCached = cachedAlbum != null
        if (isAlbumCached) return Result.success(Unit)
        return getAlbumFromApiAndSaveToCache(albumId)
    }

    private suspend fun getAlbumFromApiAndSaveToCache(albumId: Long): Result<Unit> {
        val apiResponse = albumRemoteDataSource.getAlbumWithTracks(albumId).getOrElse {
            return Result.failure(it)
        }
        val apiAlbum = apiResponse.getOnlyAlbum() ?: return Result.failure(AppError.Unknown())
        val dbTracks = apiResponse.getOnlyTracks().map(ApiTrack::toDb)
        return albumLocalDataSource.saveAlbum(apiAlbum.toDb(), dbTracks)
    }

    private suspend fun getAlbumWithTracksFromCache(albumId: Long): Result<AlbumWithTracksDomainModel> {
        val cachedAlbum = albumLocalDataSource.getAlbum(albumId).getOrElse { return Result.failure(it) }
            ?: return Result.failure(AppError.Unknown())
        val cachedTracks = albumLocalDataSource.getAlbumTracks(albumId).getOrElse { return Result.failure(it) }
        return Result.success(
            AlbumWithTracksDomainModel(album = cachedAlbum.toDomain(), tracks = cachedTracks.map(DbTrack::toDomain)),
        )
    }
}
