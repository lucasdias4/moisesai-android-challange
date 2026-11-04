package com.moisesai.challange.feature.album.presentation.composable.screen.album_tracks_section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumTrackRowComponent
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumTrack

internal val AlbumTracksTopPadding = 10.dp
internal val AlbumTracksHorizontalPadding = 20.dp

// The tracks fade in at the top and out at the bottom of the list, so they do not look cut where they leave.
private val TopFadeLength = 38.dp
private const val TOP_FADE_START_ALPHA = 0.52f
private val BottomFadeLength = 10.dp

/** The tracks of the album, in order. Tapping one reports its id to [onTrackClick]. */
@Composable
internal fun AlbumTracksSection(tracks: List<UiAlbumTrack>, onTrackClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fadedAtTopAndBottom(),
        contentPadding = PaddingValues(
            start = AlbumTracksHorizontalPadding,
            top = AlbumTracksTopPadding,
            end = AlbumTracksHorizontalPadding,
        ),
    ) {
        // A list key must be unique: the position keeps it so even if the same track id came twice.
        itemsIndexed(tracks, key = { position, track -> "${track.id}-$position" }) { _, track ->
            AlbumTrackRowComponent(track = track, onClick = { onTrackClick(track.id) })
        }
    }
}

// Draws the list, then keeps it only where a mask is opaque: clear at the very edges, solid in between.
private fun Modifier.fadedAtTopAndBottom(): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val topFadeEnd = TopFadeLength.toPx() / size.height
        val bottomFadeStart = 1f - BottomFadeLength.toPx() / size.height
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color.Black.copy(alpha = TOP_FADE_START_ALPHA),
                topFadeEnd to Color.Black,
                bottomFadeStart to Color.Black,
                1f to Color.Transparent,
            ),
            blendMode = BlendMode.DstIn,
        )
    }

@FullPreview
@Composable
private fun AlbumTracksSectionPreview() = PreviewSurface {
    AlbumTracksSection(tracks = SampleAlbum.tracks, onTrackClick = {})
}
