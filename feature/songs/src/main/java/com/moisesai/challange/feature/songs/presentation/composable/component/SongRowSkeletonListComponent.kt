package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.moisesai.challange.core.design.animation.itemAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface

// Rows of different widths look like real names; the three widths repeat down the list.
private val TitleWidthParts = floatArrayOf(0.6f, 0.45f, 0.7f)
private val ArtistWidthParts = floatArrayOf(0.35f, 0.3f, 0.4f)

/** [count] skeleton rows, for the songs that are on their way. */
internal fun LazyListScope.songRowSkeletonListComponent(count: Int, keyPrefix: String) {
    items(count = count, key = { position -> "$keyPrefix-$position" }) { position ->
        SongRowSkeletonComponent(
            titleWidthPart = TitleWidthParts[position % TitleWidthParts.size],
            artistWidthPart = ArtistWidthParts[position % ArtistWidthParts.size],
            // The skeleton rows fade in and out like real rows, so they hand over to the songs smoothly.
            modifier = itemAnimation(),
        )
    }
}

@FullPreview
@Composable
private fun SongRowSkeletonListPreview() = PreviewSurface {
    LazyColumn { songRowSkeletonListComponent(count = 5, keyPrefix = "preview") }
}
