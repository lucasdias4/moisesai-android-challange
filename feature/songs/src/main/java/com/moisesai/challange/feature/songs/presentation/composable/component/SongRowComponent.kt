package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.animation.SharedCoverAnimation
import com.moisesai.challange.core.design.animation.pressAnimation
import com.moisesai.challange.core.design.components.CoverImageComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.LongTitleSample
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

// The rows of the list and the skeleton that waits for them share these sizes, so nothing moves when the songs arrive.
internal val SongRowVerticalPadding = 8.dp
internal val SongRowCoverSize = 52.dp
internal val SongRowCoverTextGap = 16.dp
internal val SongRowNamesGap = 4.dp
private val SpaceBetweenTextAndMoreButton = 8.dp
private val MoreButtonSize = 36.dp
private val MoreIconSize = 20.dp

/**
 * One song in a list: its cover, its name, its artist and the "more" button. Tapping the song calls [onClick], tapping
 * the button calls [onMoreClick].
 */
@Composable
internal fun SongRowComponent(track: UiTrack, onClick: () -> Unit, onMoreClick: () -> Unit, modifier: Modifier = Modifier) {
    val songDescription = stringResource(R.string.songs_track_description, track.name, track.artistName)
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = SongRowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(SpaceBetweenTextAndMoreButton),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                // While pressed, the row shrinks a little and springs back.
                .pressAnimation(onClick = onClick)
                .clip(MaterialTheme.shapes.small)
                .semantics(mergeDescendants = true) { contentDescription = songDescription },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Cover(trackId = track.id, artUrl = track.artUrl)
            SongNames(track = track, modifier = Modifier.weight(1f))
        }
        MoreButton(onClick = onMoreClick)
    }
}

@Composable
private fun Cover(trackId: Long, artUrl: String?) {
    // Shared animation: the cover flies to the player when this song is opened, and back when the player closes.
    SharedCoverAnimation(trackId = trackId) {
        // The cover shimmers until the picture arrives and then fades in.
        CoverImageComponent(artUrl = artUrl, modifier = Modifier.size(SongRowCoverSize).clip(MaterialTheme.shapes.small))
    }
}

@Composable
private fun SongNames(track: UiTrack, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Column(modifier = modifier.padding(start = SongRowCoverTextGap), verticalArrangement = Arrangement.spacedBy(SongRowNamesGap)) {
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

// A round button of 36 with the three dots in the middle.
@Composable
private fun MoreButton(onClick: () -> Unit) {
    Box(
        // While pressed, the button shrinks a little and springs back.
        modifier = Modifier.size(MoreButtonSize).pressAnimation(role = Role.Button, onClick = onClick).clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_more_small),
            contentDescription = stringResource(R.string.common_more_options),
            modifier = Modifier.size(MoreIconSize),
            tint = MaterialTheme.design.colors.element03,
        )
    }
}

@FullPreview
@Composable
private fun SongRowPreview() = PreviewSurface { SongRowComponent(track = SampleTracks.first(), onClick = {}, onMoreClick = {}) }

@FullPreview
@Composable
private fun SongRowWithLongNamesPreview() = PreviewSurface {
    SongRowComponent(track = LongTitleSample, onClick = {}, onMoreClick = {})
}
