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

private val PlayPauseButtonSize = 72.dp
private val PlayPauseIconSize = 37.dp

/**
 * The big round button that plays and pauses the song: a play icon, or a pause icon while it plays, on a faint white
 * circle.
 */
@Composable
internal fun PlayPauseButtonComponent(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconRes = if (isPlaying) DesignR.drawable.ic_pause else DesignR.drawable.ic_play
    val contentDescriptionRes = if (isPlaying) R.string.songdetails_pause else R.string.songdetails_play
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
            tint = MaterialTheme.design.colors.text03,
        )
    }
}

@FullPreview
@Composable
private fun PlayPauseButtonPausedPreview() =
    PreviewSurface { PlayPauseButtonComponent(isPlaying = false, onClick = {}) }

@FullPreview
@Composable
private fun PlayPauseButtonPlayingPreview() =
    PreviewSurface { PlayPauseButtonComponent(isPlaying = true, onClick = {}) }
