package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.moisesai.challange.core.design.graphics.cssBoxShadow
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.text.lineBox
import com.moisesai.challange.core.design.theme.AuroraNight
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.cardRevealAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.LongTitleSample
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.composable.model.UiReveal
import com.moisesai.challange.feature.songs.presentation.model.UiTrack

private val CardHeight = 196.dp
private val CardTextStart = 20.dp
private val CardTextEnd = 70.dp
private val CardTextBottom = 16.dp

// The cover fades into the background colour from 30% of the height down, so the title is readable on any artwork.
private const val SCRIM_START = 0.3f
private const val SCRIM_END_ALPHA = 0.9f

private val BadgeStart = 18.dp
private val BadgeTop = 14.dp
private val BadgeHorizontalPadding = 10.dp
private val BadgeVerticalPadding = 4.dp
private val BadgeBlur = 8.dp

private val PlayButtonSize = 46.dp
private val PlayButtonInset = 16.dp
private val PlayButtonShadowBlur = 24.dp
private val PlayButtonShadowOffset = 10.dp
private const val PLAY_BUTTON_SHADOW_ALPHA = 0.5f

// The artist's line is as tall as the title's text size would make it (21 dp), not as its own 13 sp would.
private val ArtistLineHeight = 21.sp

/** A large artwork card for a track, with a [badge] such as "Just played". [cardIndex] orders its arrival. */
@Composable
internal fun ArtworkCardComponent(
    track: UiTrack,
    badge: String,
    onClick: () -> Unit,
    reveal: UiReveal,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val aurora = MaterialTheme.aurora
    val description = stringResource(R.string.songs_track_description, track.name, track.artistName)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .cardRevealAnimation("card-${track.id}-${reveal.index}", reveal.index, reveal.tracker)
            .height(CardHeight)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            // One item for a screen reader: the song and its artist read together.
            .semantics(mergeDescendants = true) { contentDescription = description }
            .testTag("songs-card-${reveal.index}"),
    ) {
        val cardWidth = maxWidth
        AsyncImage(
            model = track.largeArtUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(colors.surfaceVariant),
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to AuroraNight.copy(alpha = 0f),
                    SCRIM_START to AuroraNight.copy(alpha = 0f),
                    1f to AuroraNight.copy(alpha = SCRIM_END_ALPHA),
                ),
            ),
        )
        CardBadgeComponent(text = badge, artUrl = track.largeArtUrl, cardWidth = cardWidth)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = CardTextStart, end = CardTextEnd, bottom = CardTextBottom),
        ) {
            Text(
                text = track.name,
                style = aurora.text.cardTitle,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.lineBox(aurora.text.cardTitle),
            )
            Text(
                text = track.artistName,
                style = aurora.text.caption.copy(lineHeight = ArtistLineHeight),
                color = aurora.colors.cardArtist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        PlayButtonComponent(modifier = Modifier.align(Alignment.BottomEnd).padding(PlayButtonInset))
    }
}

@Composable
private fun CardBadgeComponent(text: String, artUrl: String?, cardWidth: Dp) {
    val aurora = MaterialTheme.aurora
    val shape = MaterialTheme.shapes.extraSmall
    Box(modifier = Modifier.offset(BadgeStart, BadgeTop).clip(shape)) {
        // The badge frosts what is behind it: a blurred copy of the cover, laid exactly where the cover is.
        AsyncImage(
            model = artUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                // The badge size comes from its text only: the copy fills the badge box, then overflows it
                // without growing it, and the clip above cuts it down to the badge.
                .matchParentSize()
                .wrapContentSize(Alignment.TopStart, unbounded = true)
                .requiredSize(cardWidth, CardHeight)
                .offset(-BadgeStart, -BadgeTop)
                .blur(BadgeBlur),
        )
        Text(
            text = text,
            style = aurora.text.badge,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier
                .background(aurora.colors.badgeFill)
                .padding(horizontal = BadgeHorizontalPadding, vertical = BadgeVerticalPadding),
        )
    }
}

@Composable
private fun PlayButtonComponent(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .cssBoxShadow(
                CircleShape,
                colors.primary.copy(alpha = PLAY_BUTTON_SHADOW_ALPHA),
                blur = PlayButtonShadowBlur,
                offsetY = PlayButtonShadowOffset,
            )
            .clip(CircleShape)
            .background(colors.primary),
    ) {
        PlayGlyphComponent(color = colors.onPrimary)
    }
}

// The cards of a preview are already in place: their arrival animation is for the app, not for a still picture.
private fun revealedTracker(vararg keys: String) = RevealTracker().apply { keys.forEach(::markRevealed) }

@FullPreview
@Composable
private fun ArtworkCardSuggestedPreview() = PreviewSurface {
    val track = SuggestedSample.first()
    ArtworkCardComponent(
        track = track,
        badge = "SUGGESTED",
        onClick = {},
        reveal = UiReveal(0, revealedTracker("card-${track.id}-0")),
    )
}

@FullPreview
@Composable
private fun ArtworkCardLongTitlePreview() = PreviewSurface {
    ArtworkCardComponent(
        track = LongTitleSample,
        badge = "JUST PLAYED",
        onClick = {},
        reveal = UiReveal(0, revealedTracker("card-${LongTitleSample.id}-0")),
    )
}

@FullPreview
@Composable
private fun CardBadgePreview() = PreviewSurface {
    CardBadgeComponent(text = "SUGGESTED", artUrl = null, cardWidth = 328.dp)
}

@FullPreview
@Composable
private fun PlayButtonPreview() = PreviewSurface { PlayButtonComponent() }
