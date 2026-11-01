package com.moisesai.challange.core.network.api

import com.moisesai.challange.core.network.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ItunesApi {
    @GET("search?media=music&entity=song")
    suspend fun searchTracksBySearchText(
        @Query("term") searchText: String,
        @Query("limit") limit: Int,
    ): ApiResponse

    /** Returns the album first, then its tracks. */
    @GET("lookup?entity=song")
    suspend fun lookupAlbumWithTracks(@Query("id") albumId: Long): ApiResponse

    @GET("lookup")
    suspend fun lookupTrack(@Query("id") trackId: Long): ApiResponse
}
