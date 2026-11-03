package com.moisesai.challange.feature.album.data.mapper

import com.moisesai.challange.core.database.entity.DbAlbum
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.album.domain.model.AlbumDomainModel

internal fun ApiAlbum.toDb() = DbAlbum(
    id = id,
    name = name,
    artistName = artistName,
    artUrl = artUrl,
)

internal fun ApiTrack.toDb() = DbTrack(
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

internal fun DbAlbum.toDomain() = AlbumDomainModel(
    id = id,
    name = name,
    artistName = artistName,
    artUrl = artUrl,
)

internal fun DbTrack.toDomain() = TrackDomainModel(
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
