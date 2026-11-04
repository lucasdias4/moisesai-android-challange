package com.moisesai.challange.feature.songdetails.presentation.composable.screen.playback_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.SkeletonBlockComponent
import com.moisesai.challange.core.design.components.SkeletonTextComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.songdetails.presentation.composable.component.PlayPauseButtonSize
import com.moisesai.challange.feature.songdetails.presentation.composable.component.SeekBarHeight
import com.moisesai.challange.feature.songdetails.presentation.composable.component.SeekBarLineHeight
import com.moisesai.challange.feature.songdetails.presentation.composable.component.SeekBarLineTop
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackNamesGap

// How wide the two names are, as a part of the width: a long track name and a shorter artist name.
private const val TRACK_NAME_WIDTH_PART = 0.6f
private const val ARTIST_NAME_WIDTH_PART = 0.35f

// The two times under the bar are short, like "0:00" and "-0:30".
private val TimeWidth = 32.dp

/** Stands where [PlaybackSection] will be while the track is prepared: its names, its bar and its play button, in the same places. */
@Composable
internal fun PlaybackSkeletonSection(modifier: Modifier = Modifier) {
    val text = MaterialTheme.design.text
    PlaybackSectionLayout(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(TrackNamesGap)) {
            // Gray bars with a shimmer where the names, the bar, the times and the play button will be, while the track is prepared.
            SkeletonTextComponent(style = text.display32, modifier = Modifier.fillMaxWidth(TRACK_NAME_WIDTH_PART))
            SkeletonTextComponent(style = text.text16, modifier = Modifier.fillMaxWidth(ARTIST_NAME_WIDTH_PART))
        }
        Column(verticalArrangement = Arrangement.spacedBy(SeekBarTimesGap)) {
            Box(modifier = Modifier.fillMaxWidth().height(SeekBarHeight)) {
                SkeletonBlockComponent(
                    modifier = Modifier.offset(y = SeekBarLineTop).fillMaxWidth().height(SeekBarLineHeight),
                    shape = CircleShape,
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SkeletonTextComponent(style = text.text14, modifier = Modifier.width(TimeWidth))
                SkeletonTextComponent(style = text.text14, modifier = Modifier.width(TimeWidth))
            }
        }
        SkeletonBlockComponent(modifier = Modifier.size(PlayPauseButtonSize), shape = CircleShape)
    }
}

@FullPreview
@Composable
private fun PlaybackSkeletonSectionPreview() = PreviewSurface { PlaybackSkeletonSection() }
