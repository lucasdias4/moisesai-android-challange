package com.moisesai.challange.core.database.support

import com.moisesai.challange.core.database.entity.DbAlbum
import com.moisesai.challange.core.database.entity.DbTrack

/** A track with every field set, so what a datasource hands to a DAO can be compared as a whole. */
internal fun aDbTrack(id: Long = 1) = DbTrack(
    id = id,
    name = "Track $id",
    artistName = "Artist",
    albumId = 100,
    albumName = "Album",
    artUrl = "https://art/$id/100x100bb.jpg",
    previewUrl = "https://preview/$id.m4a",
    trackTimeMillis = 156_629,
    releaseDate = "2020-12-11T08:00:00Z",
    trackNumber = 3,
)

/** An album with every field set. */
internal fun aDbAlbum(id: Long = 100) = DbAlbum(
    id = id,
    name = "Album $id",
    artistName = "Artist",
    artUrl = "https://art/$id/100x100bb.jpg",
)
