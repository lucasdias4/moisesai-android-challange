package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.network.api.ItunesApi
import com.moisesai.challange.core.network.extension.remoteCall
import com.moisesai.challange.core.network.model.ApiResponse
import javax.inject.Inject

/**
 * There is no `offset` parameter on purpose: the iTunes Search API ignores it, so paging happens
 * on cached results, not on the request.
 */
class TrackRemoteDataSource @Inject constructor(
    private val api: ItunesApi,
) {
    suspend fun searchTracksBySearchText(searchText: String, limit: Int): Result<ApiResponse> =
        remoteCall { api.searchTracksBySearchText(searchText, limit) }

    suspend fun getTrack(trackId: Long): Result<ApiResponse> =
        remoteCall { api.lookupTrack(trackId) }
}
