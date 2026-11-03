package com.moisesai.challange.feature.album.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.moisesai.challange.core.design.model.UiBoxShadow
import com.moisesai.challange.core.design.extension.cssBoxShadows
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val AlbumCoverSize = 120.dp
private val CoverCorner = 20.dp
private val EmptyCoverIconSize = 48.dp

// The design stacks three soft black shadows under the cover: a tight one, a wide one and a very wide one.
private val TightShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.109f), blur = 5.39.dp, offsetY = 0.27.dp)
private val WideShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.161f), blur = 18.09.dp, offsetY = 0.89.dp)
private val VeryWideShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.27f), blur = 81.dp, offsetY = 4.dp)

/** The cover of the album, square with round corners and a shadow. Without a picture it shows a list icon. */
@Composable
internal fun AlbumCoverComponent(artUrl: String?, modifier: Modifier = Modifier) {
    val coverShape = RoundedCornerShape(CoverCorner)
    Box(
        modifier = modifier
            .size(AlbumCoverSize)
            .cssBoxShadows(coverShape) { listOf(TightShadow, WideShadow, VeryWideShadow) }
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
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@FullPreview
@Composable
private fun AlbumCoverPreview() = PreviewSurface { AlbumCoverComponent(artUrl = null) }
