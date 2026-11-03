package com.moisesai.challange.feature.songdetails.presentation.composable.screen.cover_section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songdetails.presentation.composable.component.TrackCoverComponent

// The design puts the cover this far below the top bar.
private val SpaceAboveCover = 82.dp

/** The cover of the track, in the middle of the width. */
@Composable
internal fun CoverSection(artUrl: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(top = SpaceAboveCover), contentAlignment = Alignment.TopCenter) {
        TrackCoverComponent(artUrl = artUrl)
    }
}

@FullPreview
@Composable
private fun CoverSectionPreview() = PreviewSurface { CoverSection(artUrl = null) }
