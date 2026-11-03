package com.moisesai.challange.feature.songdetails.data.mapper

import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

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

internal fun TrackDomainModel.toDb() = DbTrack(
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
