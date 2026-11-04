package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.CoverImageComponent
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

internal val TrackCoverMaxSize = 264.dp
internal val TrackCoverCorner = 20.dp
private val EmptyCoverIconSize = 48.dp

/**
 * The cover of the song that is playing, square with round corners. It shimmers while the picture comes; without one it shows a list icon.
 * It is [TrackCoverMaxSize] at most and shrinks to the room it is given, so a short screen (landscape, a small phone) still shows it.
 */
@Composable
internal fun TrackCoverComponent(artUrl: String?, thumbnailArtUrl: String?, modifier: Modifier = Modifier) {
    val coverShape = RoundedCornerShape(TrackCoverCorner)
    // The cover shimmers until the picture arrives and then fades in; the small picture from the list shows meanwhile.
    CoverImageComponent(
        artUrl = artUrl,
        placeholderArtUrl = thumbnailArtUrl,
        modifier = modifier.sizeIn(maxWidth = TrackCoverMaxSize, maxHeight = TrackCoverMaxSize).aspectRatio(1f).clip(coverShape),
        emptyContent = {
            Icon(
                painter = painterResource(DesignR.drawable.ic_music_list),
                contentDescription = null,
                modifier = Modifier.size(EmptyCoverIconSize),
                tint = MaterialTheme.design.colors.element05,
            )
        },
    )
}

@FullPreview
@Composable
private fun TrackCoverPreview() = PreviewSurface { TrackCoverComponent(artUrl = null, thumbnailArtUrl = null) }
