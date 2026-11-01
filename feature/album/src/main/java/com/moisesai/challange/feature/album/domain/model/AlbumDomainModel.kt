package com.moisesai.challange.feature.album.domain.model

data class AlbumDomainModel(
    val id: Long,
    val name: String,
    val artistName: String,
    val artUrl: String?,
    val releaseDate: String?,
    val trackCount: Int,
)
