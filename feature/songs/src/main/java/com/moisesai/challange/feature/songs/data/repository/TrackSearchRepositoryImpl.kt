package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.common.DispatcherProvider
import com.moisesai.challange.core.common.TimeProvider
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.feature.songs.data.extension.normalizeSearchText
import com.moisesai.challange.feature.songs.data.mapper.toDbTracks
import com.moisesai.challange.feature.songs.data.mapper.toTrackPageDomainModel
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
    // Injected to facilitate unit testing
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatcherProvider,
) : TrackSearchRepository {

    override suspend fun searchTracksBySearchText(searchText: String, page: Int, pageSize: Int): Result<TrackPageDomainModel> {
        val normalizedSearchText = searchText.normalizeSearchText()
        // Later pages only read the cached copy, so the list does not change while the user scrolls.
        return if (page == FIRST_PAGE) {
            getFirstPageFromCacheOrApi(normalizedSearchText, pageSize)
        } else {
            getCachedPage(normalizedSearchText, page, pageSize)
        }
    }

    override suspend fun refreshTracksBySearchText(searchText: String, pageSize: Int): Result<TrackPageDomainModel> =
        getFirstPageFromApi(searchText.normalizeSearchText(), pageSize)

    private suspend fun getFirstPageFromCacheOrApi(normalizedSearchText: String, pageSize: Int): Result<TrackPageDomainModel> {
        val cachedSearch = savedSearchLocalDataSource.getSavedSearch(normalizedSearchText).getOrElse { return Result.failure(it) }
        val isSearchCached = cachedSearch != null
        return if (isSearchCached) {
            getCachedPage(normalizedSearchText, FIRST_PAGE, pageSize)
        } else {
            getFirstPageFromApi(normalizedSearchText, pageSize)
        }
    }

    private suspend fun getFirstPageFromApi(normalizedSearchText: String, pageSize: Int): Result<TrackPageDomainModel> {
        getSearchFromApiAndSaveToCache(normalizedSearchText).onFailure { return Result.failure(it) }
        return getCachedPage(normalizedSearchText, FIRST_PAGE, pageSize)
    }

    private suspend fun getSearchFromApiAndSaveToCache(normalizedSearchText: String): Result<Unit> {
        val searchTracksResponse = trackRemoteDataSource.searchTracksBySearchText(normalizedSearchText, SEARCH_FETCH_LIMIT)
            .getOrElse { return Result.failure(it) }
        // Switch dispatchers for this potentially heavy operation.
        // It currently processes 200 items, but this number may grow in the future.
        val tracks = withContext(dispatchers.default) { searchTracksResponse.toDbTracks() }
        return savedSearchLocalDataSource.saveSearch(DbSavedSearch(normalizedSearchText, timeProvider.currentTimeMillis()), tracks)
    }

    private suspend fun getCachedPage(normalizedSearchText: String, page: Int, pageSize: Int): Result<TrackPageDomainModel> =
        // One row more than the page tells whether a next page exists, without a count query.
        savedSearchLocalDataSource.getSavedSearchTracksPage(normalizedSearchText, limit = pageSize + 1, offset = page * pageSize)
            .map { tracks -> tracks.toTrackPageDomainModel(pageSize) }
}
