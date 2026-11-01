package com.moisesai.challange.core.database.datasource

import androidx.room3.withWriteTransaction
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.SavedSearchDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbSavedSearchTrack
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.datacommon.safeCall
import javax.inject.Inject

/** The searches whose results were saved, so they can be shown a page at a time, offline. */
class SavedSearchLocalDataSource @Inject internal constructor(
    private val database: AppDatabase,
    private val savedSearchDao: SavedSearchDao,
    private val trackDao: TrackDao,
) {
    /** Null when the search was never saved. */
    suspend fun getSavedSearch(searchText: String): Result<DbSavedSearch?> =
        safeCall { savedSearchDao.getSavedSearch(searchText) }

    /** One page of a saved search's tracks, in the order they were saved. Empty when the search was never saved. */
    suspend fun getSavedSearchTracksPage(searchText: String, limit: Int, offset: Int): Result<List<DbTrack>> =
        safeCall { savedSearchDao.getSavedSearchTracksPage(searchText, limit, offset) }

    /** Replaces the earlier save of the same search. The tracks are kept in the order given. */
    suspend fun saveSearch(savedSearch: DbSavedSearch, tracks: List<DbTrack>): Result<Unit> = safeCall {
        database.withWriteTransaction {
            savedSearchDao.deleteSavedSearch(savedSearch.searchText)
            trackDao.upsertTracks(tracks)
            savedSearchDao.upsertSavedSearch(savedSearch)
            savedSearchDao.insertSavedSearchTracks(savedSearch.positionedTracksOf(tracks))
        }
    }

    private fun DbSavedSearch.positionedTracksOf(tracks: List<DbTrack>): List<DbSavedSearchTrack> =
        tracks.mapIndexed { position, track -> DbSavedSearchTrack(searchText, position, track.id) }
}
