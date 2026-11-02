package com.moisesai.challange.feature.songs.presentation.composable.model

import androidx.compose.ui.Modifier
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker

/** What every songs list variant of the list needs: who it reports to, how visible its items are, and what arrived. */
internal class ListContext(
    val actions: SongsActions,
    val swapModifier: Modifier,
    val tracker: RevealTracker,
)
