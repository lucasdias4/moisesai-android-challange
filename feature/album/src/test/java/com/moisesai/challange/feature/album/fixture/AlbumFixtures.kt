package com.moisesai.challange.feature.album.fixture

import com.moisesai.challange.core.database.entity.DbAlbum
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.album.domain.model.AlbumDomainModel
import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel

// The values come from a real iTunes lookup of the album 6828810178 (`core/network/src/test/resources/fixtures/lookup_album.json`).
const val ALBUM_ID = 6828810178L
const val ALBUM_NAME = "Willie Nelson American Outlaw (Live)"
const val ALBUM_ARTIST = "Willie Nelson"
const val ALBUM_ART_URL =
    "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/17/b7/83/17b78368-0010-d048-a9d8-882433dc6953/00818914027514_Cover.jpg/100x100bb.jpg"
private const val ALBUM_RELEASE_DATE = "2020-12-11T08:00:00Z"
private const val ALBUM_TRACK_COUNT = 24
const val FIRST_TRACK_ID = 6828810179L
const val FIRST_TRACK_NAME = "Whiskey River (Live)"
const val FIRST_TRACK_ARTISTS = "Chris Stapleton, Willie Nelson"
private const val FIRST_TRACK_PREVIEW_URL =
    "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/82/b4/b9/82b4b9b6-cfcb-5d48-863d-b8be2f4bde57/mzaf_4358143386821397211.plus.aac.p.m4a"
private const val FIRST_TRACK_DURATION_MILLIS = 233_280L

fun anApiAlbum() = ApiAlbum(
    id = ALBUM_ID,
    name = ALBUM_NAME,
    artistName = ALBUM_ARTIST,
    artUrl = ALBUM_ART_URL,
    releaseDate = ALBUM_RELEASE_DATE,
    trackCount = ALBUM_TRACK_COUNT,
)

fun anApiTrack(artistName: String = FIRST_TRACK_ARTISTS) = ApiTrack(
    id = FIRST_TRACK_ID,
    name = FIRST_TRACK_NAME,
    artistName = artistName,
    albumId = ALBUM_ID,
    albumName = ALBUM_NAME,
    artUrl = ALBUM_ART_URL,
    previewUrl = FIRST_TRACK_PREVIEW_URL,
    trackTimeMillis = FIRST_TRACK_DURATION_MILLIS,
    releaseDate = ALBUM_RELEASE_DATE,
    trackNumber = 1,
)

fun aTrackDomainModel(artistName: String = FIRST_TRACK_ARTISTS) = TrackDomainModel(
    id = FIRST_TRACK_ID,
    name = FIRST_TRACK_NAME,
    artistName = artistName,
    albumId = ALBUM_ID,
    albumName = ALBUM_NAME,
    artUrl = ALBUM_ART_URL,
    previewUrl = FIRST_TRACK_PREVIEW_URL,
    trackTimeMillis = FIRST_TRACK_DURATION_MILLIS,
    releaseDate = ALBUM_RELEASE_DATE,
    trackNumber = 1,
)

fun aDbAlbum() = DbAlbum(
    id = ALBUM_ID,
    name = ALBUM_NAME,
    artistName = ALBUM_ARTIST,
    artUrl = ALBUM_ART_URL,
)

fun aDbTrack() = DbTrack(
    id = FIRST_TRACK_ID,
    name = FIRST_TRACK_NAME,
    artistName = FIRST_TRACK_ARTISTS,
    albumId = ALBUM_ID,
    albumName = ALBUM_NAME,
    artUrl = ALBUM_ART_URL,
    previewUrl = FIRST_TRACK_PREVIEW_URL,
    trackTimeMillis = FIRST_TRACK_DURATION_MILLIS,
    releaseDate = ALBUM_RELEASE_DATE,
    trackNumber = 1,
)

internal fun anAlbumDomainModel() = AlbumDomainModel(
    id = ALBUM_ID,
    name = ALBUM_NAME,
    artistName = ALBUM_ARTIST,
    artUrl = ALBUM_ART_URL,
)

internal fun anAlbumWithTracksDomainModel() = AlbumWithTracksDomainModel(album = anAlbumDomainModel(), tracks = listOf(aTrackDomainModel()))
