package com.moisesai.challange.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbSavedSearchTrack
import com.moisesai.challange.core.database.entity.DbTrack

/** Reads and writes the saved searches and the tracks of each, in order. */
@Dao
internal interface SavedSearchDao {
    /** Null when the search was never saved. */
    @Query("SELECT * FROM saved_search WHERE searchText = :searchText")
    suspend fun getSavedSearch(searchText: String): DbSavedSearch?

    /** One page of a saved search's tracks, in the order they were saved. */
    @Query(
        """
        SELECT track.* FROM saved_search_track
        INNER JOIN track ON track.id = saved_search_track.trackId
        WHERE saved_search_track.searchText = :searchText
        ORDER BY saved_search_track.position
        LIMIT :limit OFFSET :offset
        """,
    )
    suspend fun getSavedSearchTracksPage(searchText: String, limit: Int, offset: Int): List<DbTrack>

    @Upsert
    suspend fun upsertSavedSearch(savedSearch: DbSavedSearch)

    @Insert
    suspend fun insertSavedSearchTracks(savedSearchTracks: List<DbSavedSearchTrack>)

    @Query("DELETE FROM saved_search WHERE searchText = :searchText")
    suspend fun deleteSavedSearch(searchText: String)
}
