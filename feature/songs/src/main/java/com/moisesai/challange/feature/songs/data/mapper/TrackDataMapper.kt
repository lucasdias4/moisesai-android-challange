package com.moisesai.challange.feature.songs.data.mapper

import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.extension.getOnlyTracks
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel

/** The tracks of a response that mixes kinds of item, ready to be saved. Items that are not tracks are left out. */
internal fun ApiResponse.toDbTracks(): List<DbTrack> = getOnlyTracks().map(ApiTrack::toDb)

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

/**
 * The page of [pageSize] tracks that the database rows hold. The query asks for one row more than the page, so a
 * row beyond [pageSize] tells that a next page exists, without a count query.
 */
internal fun List<DbTrack>.toTrackPageDomainModel(pageSize: Int) =
    TrackPageDomainModel(tracks = take(pageSize).map(DbTrack::toDomain), hasNext = size > pageSize)
