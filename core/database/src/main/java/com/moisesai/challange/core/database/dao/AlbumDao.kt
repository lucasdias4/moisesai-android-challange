package com.moisesai.challange.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert
import com.moisesai.challange.core.database.entity.DbAlbum
import com.moisesai.challange.core.database.entity.DbAlbumTrack
import com.moisesai.challange.core.database.entity.DbTrack

/** Reads and writes the saved albums and the tracks of each, in order. */
@Dao
internal interface AlbumDao {
    /** Null when the album was never saved. */
    @Query("SELECT * FROM album WHERE id = :albumId")
    suspend fun getAlbum(albumId: Long): DbAlbum?

    /** The tracks of a saved album, in the order of the album. */
    @Query(
        """
        SELECT track.* FROM album_track
        INNER JOIN track ON track.id = album_track.trackId
        WHERE album_track.albumId = :albumId
        ORDER BY album_track.position
        """,
    )
    suspend fun getAlbumTracks(albumId: Long): List<DbTrack>

    // Upsert updates in place. A replace would delete the row first and cascade to the rows that point to it.
    @Upsert
    suspend fun upsertAlbum(album: DbAlbum)

    @Insert
    suspend fun insertAlbumTracks(albumTracks: List<DbAlbumTrack>)

    @Query("DELETE FROM album_track WHERE albumId = :albumId")
    suspend fun deleteAlbumTracks(albumId: Long)
}
