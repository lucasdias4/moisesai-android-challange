package com.moisesai.challange.core.network.support

import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiTrack

internal fun aTrack(id: Long = 1) = ApiTrack(
    id = id,
    name = "Track $id",
    artistName = "Artist",
    albumId = 1,
    albumName = "Album",
    artUrl = null,
    previewUrl = null,
    trackTimeMillis = 1000,
    releaseDate = null,
    trackNumber = 1,
)

internal fun anAlbum(id: Long = 1) = ApiAlbum(
    id = id,
    name = "Album $id",
    artistName = "Artist",
    artUrl = null,
    releaseDate = null,
    trackCount = 1,
)
