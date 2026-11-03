package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "album")
data class DbAlbum(
    @PrimaryKey val id: Long,
    val name: String,
    val artistName: String,
    val artUrl: String?,
)
