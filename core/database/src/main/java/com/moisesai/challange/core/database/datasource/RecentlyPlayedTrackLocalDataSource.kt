package com.moisesai.challange.core.database.datasource

import androidx.room3.withWriteTransaction
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.RecentlyPlayedTrackDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbRecentlyPlayedTrack
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.database.extension.asResult
import com.moisesai.challange.core.database.extension.localCall
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** The tracks the user played, newest first. */
class RecentlyPlayedTrackLocalDataSource @Inject internal constructor(
    private val database: AppDatabase,
    private val recentlyPlayedTrackDao: RecentlyPlayedTrackDao,
    private val trackDao: TrackDao,
) {
    // Room checks that the database is open when the DAO builds its Flow, not when it is collected.
    // Building it inside `flow` keeps that failure in the stream, as a `Result.failure`.
    /** The newest played tracks, up to [limit]. Emits again whenever the played tracks change. */
    fun observeRecentlyPlayedTracks(limit: Int): Flow<Result<List<DbTrack>>> =
        flow { emitAll(recentlyPlayedTrackDao.observeRecentlyPlayedTracks(limit)) }.asResult()

    /** Saves the track too, so a played track can always be shown offline. */
    suspend fun saveRecentlyPlayedTrack(track: DbTrack, playedAtMillis: Long): Result<Unit> = localCall {
        database.withWriteTransaction {
            trackDao.upsertTrack(track)
            recentlyPlayedTrackDao.upsertRecentlyPlayedTrack(
                DbRecentlyPlayedTrack(trackId = track.id, playedAtMillis = playedAtMillis),
            )
        }
    }
}
