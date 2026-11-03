package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

/** How long the track has played, at the start, and how long is left, at the end. */
@Composable
internal fun PlaybackTimesComponent(
    playedTimeText: String,
    remainingTimeText: String,
    modifier: Modifier = Modifier,
) {
    val style = MaterialTheme.design.text.text14
    val color = MaterialTheme.design.colors.whiteAlpha60
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = playedTimeText, style = style, color = color)
        Text(text = remainingTimeText, style = style, color = color)
    }
}

@FullPreview
@Composable
private fun PlaybackTimesPreview() =
    PreviewSurface { PlaybackTimesComponent(playedTimeText = "1:26", remainingTimeText = "-2:54") }
