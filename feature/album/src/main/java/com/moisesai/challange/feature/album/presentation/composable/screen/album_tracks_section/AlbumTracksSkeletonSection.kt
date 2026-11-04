package com.moisesai.challange.feature.album.presentation.composable.screen.album_tracks_section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumTrackRowSkeletonComponent

// Enough rows to fill a tall screen; the list does not scroll, so the rows that do not fit are never drawn.
private const val SKELETON_ROWS = 10

// Rows of different widths look like real names; the three widths repeat down the list.
private val TitleWidthParts = floatArrayOf(0.6f, 0.45f, 0.7f)
private val ArtistWidthParts = floatArrayOf(0.35f, 0.3f, 0.4f)

/** Stands where [AlbumTracksSection] will be while the album comes: rows with the size and the margins of the real ones. */
@Composable
internal fun AlbumTracksSkeletonSection(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        userScrollEnabled = false,
        contentPadding = PaddingValues(
            start = AlbumTracksHorizontalPadding,
            top = AlbumTracksTopPadding,
            end = AlbumTracksHorizontalPadding,
        ),
    ) {
        items(count = SKELETON_ROWS) { position ->
            // Skeleton rows with a shimmer where the tracks will be, while the album loads.
            AlbumTrackRowSkeletonComponent(
                titleWidthPart = TitleWidthParts[position % TitleWidthParts.size],
                artistWidthPart = ArtistWidthParts[position % ArtistWidthParts.size],
            )
        }
    }
}

@FullPreview
@Composable
private fun AlbumTracksSkeletonSectionPreview() = PreviewSurface { AlbumTracksSkeletonSection() }
