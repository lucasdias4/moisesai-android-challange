package com.moisesai.challange.feature.album.presentation.model

/** An album as the screen shows it: its names, its cover and its tracks in the order of the album. */
internal data class UiAlbum(val name: String, val artistName: String, val artUrl: String?, val tracks: List<UiAlbumTrack>)
