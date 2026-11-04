package com.moisesai.challange.feature.album.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.components.SkeletonBlockComponent
import com.moisesai.challange.core.design.components.SkeletonTextComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

/**
 * Stands where an [AlbumTrackRowComponent] will be while the album comes: a cover and two lines of text, as wide as the
 * parts [titleWidthPart] and [artistWidthPart] (0 to 1) of the space for the text. It has the size of a real row.
 */
@Composable
internal fun AlbumTrackRowSkeletonComponent(titleWidthPart: Float, artistWidthPart: Float, modifier: Modifier = Modifier) {
    val text = MaterialTheme.design.text
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = AlbumTrackRowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(AlbumTrackRowCoverTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Gray blocks with a shimmer: a cover and two lines of text, the size of a real row.
        SkeletonBlockComponent(modifier = Modifier.size(AlbumTrackRowCoverSize))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AlbumTrackRowNamesGap)) {
            SkeletonTextComponent(style = text.text16, modifier = Modifier.fillMaxWidth(titleWidthPart))
            SkeletonTextComponent(style = text.text12, modifier = Modifier.fillMaxWidth(artistWidthPart))
        }
    }
}

@FullPreview
@Composable
private fun AlbumTrackRowSkeletonPreview() = PreviewSurface { AlbumTrackRowSkeletonComponent(titleWidthPart = 0.6f, artistWidthPart = 0.35f) }
