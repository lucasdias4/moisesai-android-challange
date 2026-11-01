package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.database.extension.localCall
import javax.inject.Inject

class TrackLocalDataSource @Inject internal constructor(
    private val trackDao: TrackDao,
) {
    /** Null when the track is not cached. */
    suspend fun getTrack(trackId: Long): Result<DbTrack?> = localCall { trackDao.getTrack(trackId) }

    suspend fun saveTrack(track: DbTrack): Result<Unit> = localCall { trackDao.upsertTrack(track) }
}
