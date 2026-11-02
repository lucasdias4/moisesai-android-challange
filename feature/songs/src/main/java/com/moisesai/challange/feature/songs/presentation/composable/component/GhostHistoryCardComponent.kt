package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.IdleEqualizerAnimation
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.cardRevealAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal

private val GhostHeight = 150.dp
private val GhostContentSpacing = 12.dp
private val GhostBorderWidth = 1.5.dp

// A dashed outline reads as "an empty place waiting for something": 3 dp of line, then 2 dp of gap.
private val DashLength = 3.dp
private val DashGap = 2.dp

// The artist line of a card and the caption of this one share the same taller line (see ArtworkCard).
private val CaptionLineHeight = 17.dp

/** The placeholder that says where the played songs will appear, with the bouncing bars of the splash. */
@Composable
internal fun GhostHistoryCardComponent(reveal: UiReveal, modifier: Modifier = Modifier) {
    val aurora = MaterialTheme.aurora
    val shape = MaterialTheme.shapes.large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardRevealAnimation("ghost-${reveal.index}", reveal.index, reveal.tracker)
            .height(GhostHeight)
            .clip(shape)
            .background(aurora.colors.ghostFill)
            .drawWithContent {
                drawContent()
                // The line is drawn inside the edge: its centre is half its width in from the border box.
                val inset = GhostBorderWidth.toPx() / 2f
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = inset, top = inset, right = size.width - inset, bottom = size.height - inset,
                            radiusX = 32.dp.toPx() - inset, radiusY = 32.dp.toPx() - inset,
                        ),
                    )
                }
                drawPath(
                    path = path,
                    color = aurora.colors.ghostBorder,
                    style = Stroke(
                        width = GhostBorderWidth.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(DashLength.toPx(), DashGap.toPx())),
                    ),
                )
            }
            .semantics(mergeDescendants = true) {}
            .testTag("songs-ghost-card"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GhostContentSpacing, Alignment.CenterVertically),
    ) {
        IdleEqualizerAnimation()
        Text(
            text = stringResource(R.string.songs_empty_history_title),
            style = aurora.text.ghostTitle,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
        )
        Text(
            text = stringResource(R.string.songs_empty_history_message),
            style = aurora.text.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@FullPreview
@Composable
private fun GhostHistoryCardPreview() = PreviewSurface {
    GhostHistoryCardComponent(reveal = UiReveal(0, RevealTracker().apply { markRevealed("ghost-0") }))
}
