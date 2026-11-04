package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moisesai.challange.feature.songdetails.presentation.composable.animation.TrackChangeAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

internal val TrackNamesGap = 4.dp

/** The name of the track that is playing, large, and under it the name of the artist, a little faded. */
@Composable
internal fun TrackNamesComponent(
    trackName: String,
    artistName: String,
    modifier: Modifier = Modifier,
) {
    val design = MaterialTheme.design
    // When the track changes, the new names rise into place while they fade in, and the old ones fade out.
    TrackChangeAnimation(trackName = trackName, artistName = artistName, modifier = modifier.fillMaxWidth()) { shownTrackName, shownArtistName ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TrackNamesGap),
        ) {
            Text(
                text = shownTrackName,
                style = design.text.display32,
                color = design.colors.text03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = shownArtistName,
                style = design.text.text16,
                color = design.colors.whiteAlpha70,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@FullPreview
@Composable
private fun TrackNamesPreview() = PreviewSurface {
    TrackNamesComponent(
        trackName = "Get Lucky",
        artistName = "Daft Punk feat. Pharrell Williams",
    )
}
