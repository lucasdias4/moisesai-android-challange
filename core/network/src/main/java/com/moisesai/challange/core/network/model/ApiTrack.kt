package com.moisesai.challange.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("track")
data class ApiTrack(
    @SerialName("trackId") val id: Long,
    @SerialName("trackName") val name: String,
    val artistName: String,
    @SerialName("collectionId") val albumId: Long,
    @SerialName("collectionName") val albumName: String,
    @SerialName("artworkUrl100") val artUrl: String?,
    val previewUrl: String?,
    val trackTimeMillis: Long,
    val releaseDate: String?,
    val trackNumber: Int,
) : ApiItem
