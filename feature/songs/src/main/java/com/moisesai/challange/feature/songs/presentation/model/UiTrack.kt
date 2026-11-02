package com.moisesai.challange.feature.songs.presentation.model

/** A track as the screen shows it: text already formatted, so composables only draw. */
internal data class UiTrack(
    val id: Long,
    /** The album this track belongs to, so "View album" knows where to go. */
    val albumId: Long,
    val name: String,
    val artistName: String,
    /** Small cover for list rows. */
    val artUrl: String?,
    /** Large cover for cards. */
    val largeArtUrl: String?,
    val durationText: String,
)
