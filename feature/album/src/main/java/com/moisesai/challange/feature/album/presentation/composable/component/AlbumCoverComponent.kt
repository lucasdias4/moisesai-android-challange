package com.moisesai.challange.feature.album.presentation.composable.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.components.CoverImageComponent
import com.moisesai.challange.core.design.extension.cssBoxShadows
import com.moisesai.challange.core.design.model.UiBoxShadow
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

internal val AlbumCoverSize = 120.dp
internal val AlbumCoverCorner = 20.dp
private val EmptyCoverIconSize = 48.dp

// The design stacks three soft black shadows under the cover: a tight one, a wide one and a very wide one.
private val TightShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.109f), blur = 5.39.dp, offsetY = 0.27.dp)
private val WideShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.161f), blur = 18.09.dp, offsetY = 0.89.dp)
private val VeryWideShadow = UiBoxShadow(color = Color.Black.copy(alpha = 0.27f), blur = 81.dp, offsetY = 4.dp)

/** The cover of the album, square with round corners and a shadow. It shimmers while the picture comes; without one it shows a list icon. */
@Composable
internal fun AlbumCoverComponent(artUrl: String?, modifier: Modifier = Modifier) {
    val coverShape = RoundedCornerShape(AlbumCoverCorner)
    // The cover shimmers until the picture arrives and then fades in.
    CoverImageComponent(
        artUrl = artUrl,
        modifier = modifier
            .size(AlbumCoverSize)
            .cssBoxShadows(coverShape) { listOf(TightShadow, WideShadow, VeryWideShadow) }
            .clip(coverShape),
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
private fun AlbumCoverPreview() = PreviewSurface { AlbumCoverComponent(artUrl = null) }
