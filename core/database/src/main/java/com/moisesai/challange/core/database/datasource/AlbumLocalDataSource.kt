package com.moisesai.challange.core.database.datasource

import androidx.room3.withWriteTransaction
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.AlbumDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbAlbum
import com.moisesai.challange.core.database.entity.DbAlbumTrack
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.datacommon.safeCall
import javax.inject.Inject

/** The albums the user opened, with their tracks in order, so they can be shown again offline. */
class AlbumLocalDataSource @Inject internal constructor(
    private val database: AppDatabase,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
) {
    /** Null when the album was never saved. */
    suspend fun getAlbum(albumId: Long): Result<DbAlbum?> = safeCall { albumDao.getAlbum(albumId) }

    /** The tracks of a saved album, in the order of the album. Empty when the album was never saved. */
    suspend fun getAlbumTracks(albumId: Long): Result<List<DbTrack>> = safeCall { albumDao.getAlbumTracks(albumId) }

    /** Replaces the earlier save of the same album. The tracks are kept in the order given. */
    suspend fun saveAlbum(album: DbAlbum, tracks: List<DbTrack>): Result<Unit> = safeCall {
        database.withWriteTransaction {
            // The tracks go in first: the rows of album_track point to them, and to the album.
            trackDao.upsertTracks(tracks)
            albumDao.upsertAlbum(album)
            albumDao.deleteAlbumTracks(album.id)
            albumDao.insertAlbumTracks(album.positionedTracksOf(tracks))
        }
    }

    private fun DbAlbum.positionedTracksOf(tracks: List<DbTrack>): List<DbAlbumTrack> =
        tracks.mapIndexed { position, track -> DbAlbumTrack(id, position, track.id) }
}
