package com.moisesai.challange.feature.songdetails.presentation.composable.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// How long the new names take to arrive, and how long the old ones take to leave: the old ones go first, quickly, so they
// never sit on top of the new ones.
private const val ARRIVE_MILLIS = 250
private const val LEAVE_MILLIS = 120

// The new names rise from this part of their own height below their place.
private const val RISE_PART = 4

/**
 * When the track changes, the new names rise into place while they fade in, and the old ones fade out.
 * Used in `TrackNamesComponent`, around the two texts: `TrackChangeAnimation(trackName, artistName) { shownTrackName, shownArtistName -> Column { ... } }`.
 */
@Composable
internal fun TrackChangeAnimation(
    trackName: String,
    artistName: String,
    modifier: Modifier = Modifier,
    content: @Composable (trackName: String, artistName: String) -> Unit,
) {
    AnimatedContent(
        targetState = trackName to artistName,
        modifier = modifier,
        transitionSpec = {
            (fadeIn(tween(ARRIVE_MILLIS)) + slideInVertically(tween(ARRIVE_MILLIS)) { height -> height / RISE_PART }) togetherWith
                fadeOut(tween(LEAVE_MILLIS))
        },
        label = "trackChange",
    ) { (name, artist) -> content(name, artist) }
}
