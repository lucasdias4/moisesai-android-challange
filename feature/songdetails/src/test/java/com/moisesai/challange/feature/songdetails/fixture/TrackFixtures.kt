package com.moisesai.challange.feature.songdetails.fixture

import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

// The values come from real iTunes responses: "Levitating" from a search, and its album neighbours from a lookup of the album.
const val LEVITATING_ID = 1538003843L
const val LEVITATING_ALBUM_ID = 1538003494L
const val LEVITATING_PREVIEW_URL =
    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/59/dc/4d/59dc4dda-93ff-8f1c-c536-f005f6ea6af5/mzaf_3066686759813252385.plus.aac.p.m4a"
private const val LEVITATING_ART_URL =
    "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/d6/6c11d681-aa3a-d59e-4c2e-f77e181026ab/190295092665.jpg/100x100bb.jpg"
private const val LEVITATING_DURATION_MILLIS = 203_808L
private const val LEVITATING_RELEASE_DATE = "2020-03-27T12:00:00Z"

fun aTrackDomainModel(id: Long = LEVITATING_ID, previewUrl: String? = LEVITATING_PREVIEW_URL, artistName: String = "Dua Lipa") =
    TrackDomainModel(
        id = id,
        name = "Levitating",
        artistName = artistName,
        albumId = LEVITATING_ALBUM_ID,
        albumName = "Future Nostalgia",
        artUrl = LEVITATING_ART_URL,
        previewUrl = previewUrl,
        trackTimeMillis = LEVITATING_DURATION_MILLIS,
        releaseDate = LEVITATING_RELEASE_DATE,
        trackNumber = 5,
    )

fun anApiTrack(id: Long = LEVITATING_ID) = ApiTrack(
    id = id,
    name = "Levitating",
    artistName = "Dua Lipa",
    albumId = LEVITATING_ALBUM_ID,
    albumName = "Future Nostalgia",
    artUrl = LEVITATING_ART_URL,
    previewUrl = LEVITATING_PREVIEW_URL,
    trackTimeMillis = LEVITATING_DURATION_MILLIS,
    releaseDate = LEVITATING_RELEASE_DATE,
    trackNumber = 5,
)

fun aDbTrack(id: Long = LEVITATING_ID) = DbTrack(
    id = id,
    name = "Levitating",
    artistName = "Dua Lipa",
    albumId = LEVITATING_ALBUM_ID,
    albumName = "Future Nostalgia",
    artUrl = LEVITATING_ART_URL,
    previewUrl = LEVITATING_PREVIEW_URL,
    trackTimeMillis = LEVITATING_DURATION_MILLIS,
    releaseDate = LEVITATING_RELEASE_DATE,
    trackNumber = 5,
)
