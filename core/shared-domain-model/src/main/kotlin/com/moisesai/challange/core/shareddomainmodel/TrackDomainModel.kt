package com.moisesai.challange.core.shareddomainmodel

data class TrackDomainModel(
    val id: Long,
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
