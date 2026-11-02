package com.moisesai.challange.feature.songs.domain.repository

import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel

internal interface TrackSearchRepository {

    /** Serves a page from the saved search when there is one, and fetches and saves it otherwise. */
    suspend fun searchTracksBySearchText(searchText: String, page: Int, pageSize: Int): Result<TrackPageDomainModel>

    /** Fetches the search again, replaces the saved one and returns its first page. */
    suspend fun refreshTracksBySearchText(searchText: String, pageSize: Int): Result<TrackPageDomainModel>
}
