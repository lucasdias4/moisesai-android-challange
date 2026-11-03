package com.moisesai.challange.feature.album.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumTrack
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val RowVerticalPadding = 8.dp
private val TrackRowCoverSize = 44.dp
private val SpaceBetweenCoverAndText = 16.dp
private val SpaceBetweenTitleAndArtist = 4.dp

/** One track of the album: its cover, its name and its artist. The whole row can be clicked. */
@Composable
internal fun AlbumTrackRowComponent(track: UiAlbumTrack, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = RowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(SpaceBetweenCoverAndText),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = track.artUrl,
            contentDescription = null,
            modifier = Modifier
                .size(TrackRowCoverSize)
                .clip(MaterialTheme.shapes.small)
                .background(design.colors.alphaInvert10),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(SpaceBetweenTitleAndArtist)) {
            Text(
                text = track.name,
                style = design.text.text16,
                color = design.colors.text03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.artistName,
                style = design.text.text12,
                color = design.colors.text00,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@FullPreview
@Composable
private fun AlbumTrackRowPreview() =
    PreviewSurface { AlbumTrackRowComponent(track = SampleAlbum.tracks.first(), onClick = {}) }
