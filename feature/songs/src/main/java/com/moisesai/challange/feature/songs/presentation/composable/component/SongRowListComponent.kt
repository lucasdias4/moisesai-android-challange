package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.animation.itemAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

/**
 * Adds one row for each of [tracks]. Rows are keyed by [keyPrefix], the track and its position, so a track that
 * appears twice in one list cannot crash it.
 */
internal fun LazyListScope.songRowListComponent(tracks: List<UiTrack>, keyPrefix: String, callbacks: SongsCallbacks) {
    itemsIndexed(tracks, key = { position, track -> "$keyPrefix-${track.id}-$position" }) { _, track ->
        SongRowComponent(
            // The row fades in and out, and glides to its new place when the list changes.
            modifier = itemAnimation(),
            track = track,
            onClick = { callbacks.onTrackClick(track.id) },
            onMoreClick = { callbacks.onTrackOptionsClick(track) },
        )
    }
}

@FullPreview
@Composable
private fun SongRowListPreview() = PreviewSurface {
    LazyColumn { songRowListComponent(tracks = SampleTracks, keyPrefix = "preview", callbacks = SongsCallbacks.None) }
}
