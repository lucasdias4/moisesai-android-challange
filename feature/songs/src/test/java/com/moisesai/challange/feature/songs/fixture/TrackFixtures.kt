package com.moisesai.challange.feature.songs.fixture

import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

// The values come from a real iTunes search response for "Levitating", so the tests use data the API really returns.
private const val LEVITATING_ID = 1538003843L
private const val LEVITATING_NAME = "Levitating"
private const val LEVITATING_ARTIST = "Dua Lipa"
private const val LEVITATING_ALBUM_ID = 1538003494L
private const val LEVITATING_ALBUM = "Future Nostalgia"
private const val LEVITATING_ART_URL =
    "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/d6/6c11d681-aa3a-d59e-4c2e-f77e181026ab/190295092665.jpg/100x100bb.jpg"
private const val LEVITATING_PREVIEW_URL =
    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/59/dc/4d/59dc4dda-93ff-8f1c-c536-f005f6ea6af5/mzaf_3066686759813252385.plus.aac.p.m4a"
private const val LEVITATING_DURATION_MILLIS = 203_808L
private const val LEVITATING_RELEASE_DATE = "2020-03-27T12:00:00Z"
private const val LEVITATING_TRACK_NUMBER = 5

fun anApiTrack(id: Long = LEVITATING_ID, artUrl: String? = LEVITATING_ART_URL) = ApiTrack(
    id = id,
    name = LEVITATING_NAME,
    artistName = LEVITATING_ARTIST,
    albumId = LEVITATING_ALBUM_ID,
    albumName = LEVITATING_ALBUM,
    artUrl = artUrl,
    previewUrl = LEVITATING_PREVIEW_URL,
    trackTimeMillis = LEVITATING_DURATION_MILLIS,
    releaseDate = LEVITATING_RELEASE_DATE,
    trackNumber = LEVITATING_TRACK_NUMBER,
)

fun aDbTrack(id: Long = LEVITATING_ID, artUrl: String? = LEVITATING_ART_URL) = DbTrack(
    id = id,
    name = LEVITATING_NAME,
    artistName = LEVITATING_ARTIST,
    albumId = LEVITATING_ALBUM_ID,
    albumName = LEVITATING_ALBUM,
    artUrl = artUrl,
    previewUrl = LEVITATING_PREVIEW_URL,
    trackTimeMillis = LEVITATING_DURATION_MILLIS,
    releaseDate = LEVITATING_RELEASE_DATE,
    trackNumber = LEVITATING_TRACK_NUMBER,
)

fun aTrackDomainModel(id: Long = LEVITATING_ID, artUrl: String? = LEVITATING_ART_URL) = TrackDomainModel(
    id = id,
    name = LEVITATING_NAME,
    artistName = LEVITATING_ARTIST,
    albumId = LEVITATING_ALBUM_ID,
    albumName = LEVITATING_ALBUM,
    artUrl = artUrl,
    previewUrl = LEVITATING_PREVIEW_URL,
    trackTimeMillis = LEVITATING_DURATION_MILLIS,
    releaseDate = LEVITATING_RELEASE_DATE,
    trackNumber = LEVITATING_TRACK_NUMBER,
)
