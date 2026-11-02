package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.moisesai.challange.core.design.icon.DesignIcons
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.LongTitleSample
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private val RowVerticalPadding = 8.dp
private val CoverSize = 52.dp
private val SpaceBetweenCoverAndText = 16.dp
private val SpaceBetweenTitleAndArtist = 4.dp
private val SpaceBetweenTextAndMoreButton = 8.dp
private val MoreButtonSize = 36.dp
private val MoreIconSize = 20.dp

/** One song in a list: its cover, its name, its artist and the "more" icon. The whole row can be clicked. */
@Composable
internal fun SongRowComponent(track: UiTrack, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val songDescription = stringResource(R.string.songs_track_description, track.name, track.artistName)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = songDescription }
            .padding(vertical = RowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(SpaceBetweenTextAndMoreButton),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Cover(artUrl = track.artUrl)
        SongNames(track = track, modifier = Modifier.weight(1f))
        MoreIcon()
    }
}

@Composable
private fun Cover(artUrl: String?) {
    AsyncImage(
        model = artUrl,
        contentDescription = null,
        modifier = Modifier
            .size(CoverSize)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.design.colors.alphaInvert10),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SongNames(track: UiTrack, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Column(modifier = modifier.padding(start = SpaceBetweenCoverAndText), verticalArrangement = Arrangement.spacedBy(SpaceBetweenTitleAndArtist)) {
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

// A round touch area of 36 with the three dots in the middle. It only shows for now: the action sheet will open from it.
@Composable
private fun MoreIcon() {
    Box(modifier = Modifier.size(MoreButtonSize), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = DesignIcons.More,
            contentDescription = null,
            modifier = Modifier.size(MoreIconSize),
            tint = MaterialTheme.design.colors.element03,
        )
    }
}

@FullPreview
@Composable
private fun SongRowPreview() = PreviewSurface { SongRowComponent(track = SampleTracks.first(), onClick = {}) }

@FullPreview
@Composable
private fun SongRowWithLongNamesPreview() = PreviewSurface {
    SongRowComponent(track = LongTitleSample, onClick = {})
}
