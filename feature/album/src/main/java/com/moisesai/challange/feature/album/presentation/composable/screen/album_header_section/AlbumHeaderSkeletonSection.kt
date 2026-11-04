package com.moisesai.challange.feature.album.presentation.composable.screen.album_header_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.moisesai.challange.core.design.components.SkeletonBlockComponent
import com.moisesai.challange.core.design.components.SkeletonTextComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumCoverCorner
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumCoverSize
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumNamesGap

// How wide the two names are, as a part of the space for them: a long album name and a shorter artist name.
private const val ALBUM_NAME_WIDTH_PART = 0.6f
private const val ARTIST_NAME_WIDTH_PART = 0.35f

/** Stands where [AlbumHeaderSection] will be while the album comes: its cover and its two names, in the same places. A screen reader hears that the album is loading. */
@Composable
internal fun AlbumHeaderSkeletonSection(modifier: Modifier = Modifier) {
    val text = MaterialTheme.design.text
    val loadingDescription = stringResource(R.string.common_loading)
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = AlbumHeaderBottomPadding).semantics { contentDescription = loadingDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlbumHeaderCoverNamesGap),
    ) {
        // Gray blocks with a shimmer where the cover and the two names will be, while the album loads.
        SkeletonBlockComponent(modifier = Modifier.size(AlbumCoverSize), shape = RoundedCornerShape(AlbumCoverCorner))
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AlbumHeaderNamesHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AlbumNamesGap),
        ) {
            SkeletonTextComponent(style = text.display20, modifier = Modifier.fillMaxWidth(ALBUM_NAME_WIDTH_PART))
            SkeletonTextComponent(style = text.text14, modifier = Modifier.fillMaxWidth(ARTIST_NAME_WIDTH_PART))
        }
    }
}

@FullPreview
@Composable
private fun AlbumHeaderSkeletonSectionPreview() = PreviewSurface { AlbumHeaderSkeletonSection() }
