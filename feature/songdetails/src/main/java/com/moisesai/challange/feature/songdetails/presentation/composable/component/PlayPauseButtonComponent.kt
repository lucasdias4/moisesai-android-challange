package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

private val PlayPauseButtonSize = 72.dp
private val PlayPauseIconSize = 37.dp

// The icon of a button that cannot be used: the same dimming Material uses for disabled content.
private const val DISABLED_ICON_ALPHA = 0.38f

/**
 * The big round button that plays and pauses the song: a play icon, or a pause icon while it plays, on a faint white
 * circle. While the track cannot play ([UiPlayingTrack.canPlay] is false) it looks off but still answers the tap, so the
 * screen can say why: a button that ignores the user tells them nothing.
 */
@Composable
internal fun PlayPauseButtonComponent(
    playingTrack: UiPlayingTrack,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconRes = if (playingTrack.isPlaying) DesignR.drawable.ic_pause else DesignR.drawable.ic_play
    val contentDescriptionRes = if (playingTrack.isPlaying) R.string.songdetails_pause else R.string.songdetails_play
    val iconTint = MaterialTheme.design.colors.text03
    val iconColor = if (playingTrack.canPlay) iconTint else iconTint.copy(alpha = DISABLED_ICON_ALPHA)
    Box(
        modifier = modifier
            .size(PlayPauseButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.design.colors.whiteAlpha20)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier.size(PlayPauseIconSize),
            tint = iconColor,
        )
    }
}

@FullPreview
@Composable
private fun PlayPauseButtonPausedPreview() =
    PreviewSurface { PlayPauseButtonComponent(playingTrack = SamplePlayingTrack, onClick = {}) }

@FullPreview
@Composable
private fun PlayPauseButtonPlayingPreview() =
    PreviewSurface { PlayPauseButtonComponent(playingTrack = SamplePlayingTrack.copy(isPlaying = true), onClick = {}) }

@FullPreview
@Composable
private fun PlayPauseButtonCannotPlayPreview() =
    PreviewSurface { PlayPauseButtonComponent(playingTrack = SamplePlayingTrack.copy(canPlay = false), onClick = {}) }
