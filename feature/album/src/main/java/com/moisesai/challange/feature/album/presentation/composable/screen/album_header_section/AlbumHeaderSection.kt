package com.moisesai.challange.feature.album.presentation.composable.screen.album_header_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumCoverComponent
import com.moisesai.challange.feature.album.presentation.composable.component.AlbumNamesComponent
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbum

internal val AlbumHeaderCoverNamesGap = 16.dp
internal val AlbumHeaderNamesHorizontalPadding = 24.dp
internal val AlbumHeaderBottomPadding = 8.dp

/** What the album is: its cover, then its name and its artist. */
@Composable
internal fun AlbumHeaderSection(album: UiAlbum, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = AlbumHeaderBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlbumHeaderCoverNamesGap),
    ) {
        AlbumCoverComponent(artUrl = album.artUrl)
        AlbumNamesComponent(
            albumName = album.name,
            artistName = album.artistName,
            modifier = Modifier.padding(horizontal = AlbumHeaderNamesHorizontalPadding),
        )
    }
}

@FullPreview
@Composable
private fun AlbumHeaderSectionPreview() = PreviewSurface { AlbumHeaderSection(album = SampleAlbum) }
