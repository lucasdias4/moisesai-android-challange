package com.moisesai.challange.feature.album.domain.model

internal data class AlbumDomainModel(
    val id: Long,
    val name: String,
    val artistName: String,
    val artUrl: String?,
)
