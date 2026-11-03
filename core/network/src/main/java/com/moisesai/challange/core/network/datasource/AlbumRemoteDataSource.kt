package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.network.api.ItunesApi
import com.moisesai.challange.core.network.extension.safeRemoteCall
import com.moisesai.challange.core.network.model.ApiResponse
import javax.inject.Inject

class AlbumRemoteDataSource @Inject internal constructor(
    private val api: ItunesApi,
) {
    /** The album first, then its tracks in track order. */
    suspend fun getAlbumWithTracks(albumId: Long): Result<ApiResponse> =
        safeRemoteCall {
            api.lookupAlbumWithTracks(albumId)
        }
}
