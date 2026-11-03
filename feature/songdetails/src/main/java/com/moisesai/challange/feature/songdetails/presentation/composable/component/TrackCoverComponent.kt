package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val CoverSize = 264.dp
private val CoverCorner = 20.dp
private val EmptyCoverIconSize = 48.dp

/** The cover of the song that is playing, square with round corners. Without a picture it shows a list icon. */
@Composable
internal fun TrackCoverComponent(artUrl: String?, modifier: Modifier = Modifier) {
    val coverShape = RoundedCornerShape(CoverCorner)
    Box(
        modifier = modifier
            .size(CoverSize)
            .clip(coverShape)
            .background(MaterialTheme.design.colors.alphaInvert10),
        contentAlignment = Alignment.Center,
    ) {
        // Sits behind the picture: it is what the user sees while the picture loads, or when there is none.
        Icon(
            painter = painterResource(DesignR.drawable.ic_music_list),
            contentDescription = null,
            modifier = Modifier.size(EmptyCoverIconSize),
            tint = MaterialTheme.design.colors.element05,
        )
        AsyncImage(
            model = artUrl,
            contentDescription = null,
            modifier = Modifier.size(CoverSize),
            contentScale = ContentScale.Crop,
        )
    }
}

@FullPreview
@Composable
private fun TrackCoverPreview() = PreviewSurface { TrackCoverComponent(artUrl = null) }
