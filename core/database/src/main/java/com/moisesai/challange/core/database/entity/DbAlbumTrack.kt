package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "album_track",
    primaryKeys = ["albumId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = DbAlbum::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DbTrack::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("trackId")],
)
internal data class DbAlbumTrack(
    val albumId: Long,
    val position: Int,
    val trackId: Long,
)
