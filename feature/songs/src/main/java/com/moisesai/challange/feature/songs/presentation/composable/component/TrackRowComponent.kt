package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.moisesai.challange.core.design.animation.CssEaseEasing
import com.moisesai.challange.core.design.graphics.angledLinearGradient
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.AuroraLime
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.rowRevealAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.LongTitleSample
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private val RowHeight = 70.dp
private val RowVerticalPadding = 7.dp
private val RowStartPadding = 7.dp
private val RowEndPadding = 10.dp
private val ArtSize = 56.dp
private val RowGap = 14.dp
private val RowSpacing = 4.dp

// The artist's line is as tall as the title's text size would make it (21 dp), not as its own 13 sp would.
private val ArtistLineHeight = 21.sp

// A pressed row takes the "selected" look of the design: a faint white fill, a lit edge and a lime title.
private const val PRESSED_FILL_ALPHA = 0.10f
private const val PRESS_MILLIS = 400
private const val PRESSED_EDGE_ANGLE_DEGREES = 140f
private val PressedEdgeWidth = 1.dp
private const val SECONDARY_TEXT_ALPHA = 0.7f

private val PressedEdge = angledLinearGradient(
    PRESSED_EDGE_ANGLE_DEGREES,
    0f to Color.White.copy(alpha = 0.5f),
    0.4f to Color.White.copy(alpha = 0.05f),
    1f to AuroraLime.copy(alpha = 0.5f),
)

/** One search result: cover, name, artist and duration. [reveal] says which row it is among those that arrive together. */
@Composable
internal fun TrackRowComponent(
    track: UiTrack,
    reveal: UiReveal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressed = animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(PRESS_MILLIS, easing = CssEaseEasing),
        label = "rowPressed",
    )
    val colors = MaterialTheme.colorScheme
    val text = MaterialTheme.aurora.text
    val shape = MaterialTheme.shapes.medium
    val description = stringResource(R.string.songs_track_description, track.name, track.artistName)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .rowRevealAnimation("row-${track.id}-${reveal.index}", reveal.index, reveal.tracker)
            .height(RowHeight)
            .clip(shape)
            .drawBehind {
                val level = pressed.value.coerceIn(0f, 1f)
                drawRect(Color.White.copy(alpha = PRESSED_FILL_ALPHA * level))
                val edge = PressedEdgeWidth.toPx()
                drawRoundRect(
                    brush = PressedEdge,
                    alpha = level,
                    topLeft = Offset(edge / 2f, edge / 2f),
                    size = Size(size.width - edge, size.height - edge),
                    cornerRadius = CornerRadius(24.dp.toPx() - edge / 2f),
                    style = Stroke(edge),
                )
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(start = RowStartPadding, end = RowEndPadding, top = RowVerticalPadding, bottom = RowVerticalPadding)
            // One item for a screen reader: the song and its artist read together.
            .semantics(mergeDescendants = true) { contentDescription = description }
            .testTag("songs-row-${reveal.index}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowGap),
    ) {
        AsyncImage(
            model = track.artUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(ArtSize)
                .clip(MaterialTheme.shapes.small)
                .background(colors.surfaceVariant),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = text.rowTitle,
                color = lerp(colors.onBackground, colors.primary, pressed.value.coerceIn(0f, 1f)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.artistName,
                style = text.caption.copy(lineHeight = ArtistLineHeight),
                color = colors.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = track.durationText,
            style = text.caption,
            color = colors.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
            maxLines = 1,
        )
    }
}

@FullPreview
@Composable
private fun TrackRowPreview() = PreviewSurface {
    val track = SampleTracks.first()
    TrackRowComponent(
        track = track,
        reveal = UiReveal(0, RevealTracker().apply { markRevealed("row-${track.id}-0") }),
        onClick = {},
    )
}

@FullPreview
@Composable
private fun TrackRowLongTitlePreview() = PreviewSurface {
    TrackRowComponent(
        track = LongTitleSample,
        reveal = UiReveal(0, RevealTracker().apply { markRevealed("row-${LongTitleSample.id}-0") }),
        onClick = {},
    )
}
