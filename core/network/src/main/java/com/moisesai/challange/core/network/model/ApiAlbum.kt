package com.moisesai.challange.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("collection")
data class ApiAlbum(
    @SerialName("collectionId") val id: Long,
    @SerialName("collectionName") val name: String,
    val artistName: String,
    @SerialName("artworkUrl100") val artUrl: String?,
    val releaseDate: String?,
    val trackCount: Int,
) : ApiItem
