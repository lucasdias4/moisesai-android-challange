package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.moisesai.challange.feature.songdetails.presentation.composable.callback.SongDetailsCallbacks
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack

private val SpaceBetweenMainControls = 24.dp
private val SkipIconSize = 36.dp
private val RepeatIconSize = 24.dp
private val RepeatButtonSize = 40.dp
private const val MIRRORED_SCALE_X = -1f

/** The play button with the previous and next track beside it, and the repeat icon at the far end. */
@Composable
internal fun PlaybackControlsComponent(
    playingTrack: UiPlayingTrack,
    callbacks: SongDetailsCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceBetweenMainControls),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayPauseButtonComponent(
                isPlaying = playingTrack.isPlaying,
                onClick = callbacks.onPlayPauseClick,
            )
            PreviousButton(onClick = callbacks.onPreviousClick)
            NextButton(onClick = callbacks.onNextClick)
        }
        RepeatButton(
            isRepeatEnabled = playingTrack.isRepeatEnabled,
            onClick = callbacks.onRepeatClick,
        )
    }
}

// While repeat is on, the icon sits on the same faint circle as the play button, so the user can tell it is on.
@Composable
private fun RepeatButton(isRepeatEnabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.design.colors
    Box(
        modifier = Modifier
            .size(RepeatButtonSize)
            .clip(CircleShape)
            .background(if (isRepeatEnabled) colors.whiteAlpha20 else Color.Transparent)
            .selectable(selected = isRepeatEnabled, role = Role.Switch, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_play_on_repeat),
            contentDescription = stringResource(R.string.songdetails_repeat),
            modifier = Modifier.size(RepeatIconSize),
            tint = colors.text03,
        )
    }
}

@Composable
private fun PreviousButton(onClick: () -> Unit) {
    // The design has one skip arrow, pointing right for the next track; the previous track's one is its mirror.
    SkipButton(
        contentDescriptionRes = R.string.songdetails_previous,
        onClick = onClick,
        modifier = Modifier.scale(scaleX = MIRRORED_SCALE_X, scaleY = 1f),
    )
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    SkipButton(contentDescriptionRes = R.string.songdetails_next, onClick = onClick)
}

@Composable
private fun SkipButton(
    @StringRes contentDescriptionRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(DesignR.drawable.ic_skip_next),
        contentDescription = stringResource(contentDescriptionRes),
        modifier = modifier
            .size(SkipIconSize)
            .clickable(role = Role.Button, onClick = onClick),
        tint = MaterialTheme.design.colors.text03,
    )
}

@FullPreview
@Composable
private fun RepeatButtonOffPreview() =
    PreviewSurface { RepeatButton(isRepeatEnabled = false, onClick = {}) }

@FullPreview
@Composable
private fun RepeatButtonOnPreview() =
    PreviewSurface { RepeatButton(isRepeatEnabled = true, onClick = {}) }

@FullPreview
@Composable
private fun PlaybackControlsPreview() = PreviewSurface {
    PlaybackControlsComponent(
        playingTrack = SamplePlayingTrack,
        callbacks = SongDetailsCallbacks.None,
    )
}

@FullPreview
@Composable
private fun PlaybackControlsPlayingOnRepeatPreview() = PreviewSurface {
    PlaybackControlsComponent(
        playingTrack = SamplePlayingTrack.copy(
            isPlaying = true,
            isRepeatEnabled = true,
        ),
        callbacks = SongDetailsCallbacks.None,
    )
}
