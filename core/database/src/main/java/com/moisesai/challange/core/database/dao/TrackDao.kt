package com.moisesai.challange.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.moisesai.challange.core.database.entity.DbTrack

/** Reads and writes the tracks stored locally. Cached searches and recently played tracks both point to these rows. */
@Dao
internal interface TrackDao {
    @Query("SELECT * FROM track WHERE id = :trackId")
    suspend fun getTrack(trackId: Long): DbTrack?

    // Upsert updates in place. A replace would delete the row first and cascade to the rows that point to it.
    @Upsert
    suspend fun upsertTrack(track: DbTrack)

    @Upsert
    suspend fun upsertTracks(tracks: List<DbTrack>)
}
