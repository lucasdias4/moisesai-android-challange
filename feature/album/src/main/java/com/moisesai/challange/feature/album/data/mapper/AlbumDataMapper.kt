package com.moisesai.challange.feature.album.data.mapper

import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.album.domain.model.AlbumDomainModel

internal fun ApiAlbum.toDomain() = AlbumDomainModel(
    id = id,
    name = name,
    artistName = artistName,
    artUrl = artUrl,
)

internal fun ApiTrack.toDomain() = TrackDomainModel(
    id = id,
    name = name,
    artistName = artistName,
    albumId = albumId,
    albumName = albumName,
    artUrl = artUrl,
    previewUrl = previewUrl,
    trackTimeMillis = trackTimeMillis,
    releaseDate = releaseDate,
    trackNumber = trackNumber,
)
