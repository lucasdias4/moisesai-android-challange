package com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.extension

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import com.moisesai.challange.feature.songs.presentation.composable.component.SongRowComponent
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

/**
 * Adds one row for each of [tracks]. Rows are keyed by [keyPrefix], the track and its position, so a track that
 * appears twice in one list cannot crash it.
 */
internal fun LazyListScope.showSongRows(tracks: List<UiTrack>, keyPrefix: String, onSongClick: (Long) -> Unit) {
    itemsIndexed(tracks, key = { position, track -> "$keyPrefix-${track.id}-$position" }) { _, track ->
        SongRowComponent(track = track, onClick = { onSongClick(track.id) })
    }
}
