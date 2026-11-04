package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.network.api.ItunesApi
import com.moisesai.challange.core.network.extension.safeRemoteCall
import com.moisesai.challange.core.network.model.ApiResponse
import javax.inject.Inject

class TrackRemoteDataSource @Inject internal constructor(private val api: ItunesApi) {
    /**
     * There is no `offset` parameter on purpose: the iTunes Search API ignores it, so paging happens
     * on cached results, not on the request.
     */
    suspend fun searchTracksBySearchText(searchText: String, limit: Int): Result<ApiResponse> =
        safeRemoteCall { api.searchTracksBySearchText(searchText, limit) }

    suspend fun getTrack(trackId: Long): Result<ApiResponse> = safeRemoteCall { api.lookupTrack(trackId) }
}
