package com.moisesai.challange.feature.songs.presentation.mapper

import com.moisesai.challange.core.design.extension.mainArtist
import com.moisesai.challange.core.design.extension.toLargeCoverUrl
import com.moisesai.challange.core.design.extension.toMediumCoverUrl
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECOND_DIGITS = 2
private const val HALF_SECOND_MILLIS = 500L

// The design shows a song without the note iTunes adds at the end, such as "[From GTAVI: Original Score]" or "(feat. SZA)".
private val TrailingNote = Regex("\\s*[\\[(][^\\])]*[\\])]\\s*$")

internal fun TrackDomainModel.toUiModel() = UiTrack(
    id = id,
    albumId = albumId,
    name = name.withoutTrailingNote(),
    artistName = artistName.mainArtist(),
    artUrl = artUrl?.toMediumCoverUrl(),
    largeArtUrl = artUrl?.toLargeCoverUrl(),
    durationText = trackTimeMillis.toDurationText(),
)

// A name that is only a note, like "(Untitled)", is kept whole: removing it would leave nothing to show.
private fun String.withoutTrailingNote(): String = replace(TrailingNote, "").ifBlank { this }

// Built by hand, not with String.format, so the digits never depend on the device language.
// The seconds are rounded to the nearest, as a player would show them, and not cut off.
private fun Long.toDurationText(): String {
    val totalSeconds = (this + HALF_SECOND_MILLIS) / MILLIS_PER_SECOND
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = (totalSeconds % SECONDS_PER_MINUTE).toString().padStart(SECOND_DIGITS, '0')
    return "$minutes:$seconds"
}
