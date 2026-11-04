package com.moisesai.challange.feature.album.presentation.composable.component

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.SharedCoverAnimation
import com.moisesai.challange.core.design.animation.pressAnimation
import com.moisesai.challange.core.design.components.CoverImageComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumTrack

internal val AlbumTrackRowVerticalPadding = 8.dp
internal val AlbumTrackRowCoverSize = 44.dp
internal val AlbumTrackRowCoverTextGap = 16.dp
internal val AlbumTrackRowNamesGap = 4.dp

/** One track of the album: its cover, its name and its artist. The whole row can be clicked. */
@Composable
internal fun AlbumTrackRowComponent(track: UiAlbumTrack, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Row(
        modifier = modifier
            .fillMaxWidth()
            // While pressed, the row shrinks a little and springs back.
            .pressAnimation(onClick = onClick)
            .clip(MaterialTheme.shapes.small)
            .padding(vertical = AlbumTrackRowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(AlbumTrackRowCoverTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Shared animation: the cover flies to the player when this track is opened, and back when the player closes.
        SharedCoverAnimation(trackId = track.id) {
            // The cover shimmers until the picture arrives and then fades in.
            CoverImageComponent(artUrl = track.artUrl, modifier = Modifier.size(AlbumTrackRowCoverSize).clip(MaterialTheme.shapes.small))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AlbumTrackRowNamesGap)) {
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
private fun AlbumTrackRowPreview() = PreviewSurface { AlbumTrackRowComponent(track = SampleAlbum.tracks.first(), onClick = {}) }
