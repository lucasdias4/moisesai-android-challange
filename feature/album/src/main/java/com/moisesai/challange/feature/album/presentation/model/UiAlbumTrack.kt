package com.moisesai.challange.feature.album.presentation.model

/** A track of an album as the screen shows it. */
internal data class UiAlbumTrack(
    val id: Long,
    val name: String,
    val artistName: String,
    val artUrl: String?,
)
