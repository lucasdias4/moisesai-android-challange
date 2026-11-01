package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "track")
data class DbTrack(
    @PrimaryKey val id: Long,
    val name: String,
    val artistName: String,
    val albumId: Long,
    val albumName: String,
    val artUrl: String?,
    val previewUrl: String?,
    val trackTimeMillis: Long,
    val releaseDate: String?,
    val trackNumber: Int,
)
