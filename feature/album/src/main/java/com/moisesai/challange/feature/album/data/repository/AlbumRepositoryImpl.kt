package com.moisesai.challange.feature.album.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.common.DispatcherProvider
import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.extension.getOnlyAlbum
import com.moisesai.challange.core.network.extension.getOnlyTracks
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.feature.album.data.mapper.toDomain
import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.domain.repository.AlbumRepository
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class AlbumRepositoryImpl @Inject constructor(
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val dispatchers: DispatcherProvider,
) : AlbumRepository {

    override suspend fun getAlbumWithTracks(albumId: Long): Result<AlbumWithTracksDomainModel> {
        val apiResponse = albumRemoteDataSource.getAlbumWithTracks(albumId).getOrElse { return Result.failure(it) }
        // Mapping a whole album is the only work here that is not a datasource call, so it is the only switch of dispatcher.
        return withContext(dispatchers.default) { apiResponse.toAlbumWithTracks() }
    }

    private fun ApiResponse.toAlbumWithTracks(): Result<AlbumWithTracksDomainModel> {
        // A lookup of an id that is not an album succeeds with no album in it, which is a failure for the caller.
        val apiAlbum = getOnlyAlbum() ?: return Result.failure(AppError.Unknown())
        val tracks = getOnlyTracks().map(ApiTrack::toDomain)
        return Result.success(AlbumWithTracksDomainModel(album = apiAlbum.toDomain(), tracks = tracks))
    }
}
