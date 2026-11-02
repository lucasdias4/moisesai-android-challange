package com.moisesai.challange.feature.songs.presentation.composable.model

import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker

/** Which row or card this is among the ones that arrive together, and who remembers which already arrived. */
internal class UiReveal(val index: Int, val tracker: RevealTracker)
