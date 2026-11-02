package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.common.DispatcherProvider
import com.moisesai.challange.core.common.TimeProvider
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.tracks
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.feature.songs.data.mapper.toDb
import com.moisesai.challange.feature.songs.data.mapper.toDomain
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import javax.inject.Inject
import kotlinx.coroutines.withContext

// iTunes ignores an offset, so one request brings the most results it allows and the app pages through the saved copy.
private const val SEARCH_FETCH_LIMIT = 200
private const val FIRST_PAGE = 0

/**
 * Offline-first: a search that was saved once is served from the database and is never fetched again
 * until the caller asks for a refresh. Nothing expires and nothing is deleted.
 */
internal class TrackSearchRepositoryImpl @Inject constructor(
    private val trackRemoteDataSource: TrackRemoteDataSource,
    private val savedSearchLocalDataSource: SavedSearchLocalDataSource,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatcherProvider,
) : TrackSearchRepository {

    override suspend fun searchTracksBySearchText(searchText: String, page: Int, pageSize: Int): Result<TrackPageDomainModel> {
        val savedSearchText = normalizeSearchText(searchText)
        // Later pages only read the saved copy, so the list does not change while the user scrolls.
        if (page != FIRST_PAGE) return readPage(savedSearchText, page, pageSize)
        return savedSearchLocalDataSource.getSavedSearch(savedSearchText).flatMap { savedSearch ->
            if (savedSearch == null) refreshTracksBySearchText(savedSearchText, pageSize) else readPage(savedSearchText, FIRST_PAGE, pageSize)
        }
    }

    override suspend fun refreshTracksBySearchText(searchText: String, pageSize: Int): Result<TrackPageDomainModel> {
        val savedSearchText = normalizeSearchText(searchText)
        return fetchAndSaveSearch(savedSearchText).flatMap { readPage(savedSearchText, FIRST_PAGE, pageSize) }
    }

    private suspend fun fetchAndSaveSearch(savedSearchText: String): Result<Unit> =
        trackRemoteDataSource.searchTracksBySearchText(savedSearchText, SEARCH_FETCH_LIMIT)
            .map { response -> toDbTracks(response) }
            .flatMap { tracks ->
                savedSearchLocalDataSource.saveSearch(DbSavedSearch(savedSearchText, timeProvider.currentTimeMillis()), tracks)
            }

    // Mapping up to 200 tracks is the only work here that is not a datasource call, so it is the only switch of dispatcher.
    private suspend fun toDbTracks(response: ApiResponse): List<DbTrack> =
        withContext(dispatchers.io) { response.tracks().map(ApiTrack::toDb) }

    private suspend fun readPage(savedSearchText: String, page: Int, pageSize: Int): Result<TrackPageDomainModel> =
        // One row more than the page tells whether a next page exists, without a count query.
        savedSearchLocalDataSource.getSavedSearchTracksPage(savedSearchText, limit = pageSize + 1, offset = page * pageSize)
            .map { tracks -> tracks.toTrackPage(pageSize) }

    private fun List<DbTrack>.toTrackPage(pageSize: Int) =
        TrackPageDomainModel(tracks = take(pageSize).map(DbTrack::toDomain), hasNext = size > pageSize)
}
