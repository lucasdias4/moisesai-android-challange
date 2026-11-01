package com.moisesai.challange.core.database.support

import com.moisesai.challange.core.database.entity.DbTrack

/** A track with every field set, so a saved and read track can be compared as a whole. */
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
