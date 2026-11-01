package com.moisesai.challange.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.moisesai.challange.core.database.entity.DbRecentlyPlayedTrack
import com.moisesai.challange.core.database.entity.DbTrack
import kotlinx.coroutines.flow.Flow

/** Reads and writes the tracks the user played, newest first. */
@Dao
internal interface RecentlyPlayedTrackDao {
    @Query(
        """
        SELECT track.* FROM recently_played_track
        INNER JOIN track ON track.id = recently_played_track.trackId
        ORDER BY recently_played_track.playedAtMillis DESC
        LIMIT :limit
        """,
    )
    fun observeRecentlyPlayedTracks(limit: Int): Flow<List<DbTrack>>

    @Upsert
    suspend fun upsertRecentlyPlayedTrack(recentlyPlayedTrack: DbRecentlyPlayedTrack)
}
