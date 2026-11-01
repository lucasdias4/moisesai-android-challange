package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.datacommon.safeCall
import javax.inject.Inject

class TrackLocalDataSource @Inject internal constructor(
    private val trackDao: TrackDao,
) {
    /** Null when the track is not cached. */
    suspend fun getTrack(trackId: Long): Result<DbTrack?> = safeCall { trackDao.getTrack(trackId) }

    suspend fun saveTrack(track: DbTrack): Result<Unit> = safeCall { trackDao.upsertTrack(track) }
}
