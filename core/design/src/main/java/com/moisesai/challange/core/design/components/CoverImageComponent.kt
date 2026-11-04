package com.moisesai.challange.core.design.components

import com.moisesai.challange.core.design.animation.spec.SCREEN_MILLIS
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private enum class CoverState { Loading, Loaded, Empty }

/**
 * A cover picture that fills the space it is given. While the picture comes there is a shimmering block; when it
 * arrives it fades in. With no picture, or when it fails, [emptyContent] shows instead, in the middle. If a smaller
 * picture of the same cover is already in memory ([placeholderArtUrl]), it shows at once while the big one comes.
 */
@Composable
fun CoverImageComponent(
    artUrl: String?,
    modifier: Modifier = Modifier,
    placeholderArtUrl: String? = null,
    emptyContent: @Composable () -> Unit = {},
) {
    // A new address starts again: the shimmer comes back until the new picture is there.
    var state by remember(artUrl) { mutableStateOf(if (artUrl == null) CoverState.Empty else CoverState.Loading) }
    // The address is the key of the picture in memory, so a small picture that a list already loaded can be found by it.
    val request = ImageRequest.Builder(LocalPlatformContext.current)
        .data(artUrl)
        .memoryCacheKey(artUrl)
        .placeholderMemoryCacheKey(placeholderArtUrl?.let { MemoryCache.Key(it) })
        .crossfade(SCREEN_MILLIS)
        .build()
    Box(modifier = modifier.background(MaterialTheme.design.colors.alphaInvert10), contentAlignment = Alignment.Center) {
        when (state) {
            CoverState.Loading -> SkeletonBlockComponent(modifier = Modifier.fillMaxSize(), shape = RectangleShape)
            CoverState.Empty -> emptyContent()
            CoverState.Loaded -> Unit
        }
        if (artUrl != null) {
            AsyncImage(
                model = request,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onState = { imageState ->
                    state = when (imageState) {
                        is AsyncImagePainter.State.Success -> CoverState.Loaded
                        is AsyncImagePainter.State.Error -> CoverState.Empty
                        else -> state
                    }
                },
            )
        }
    }
}

@FullPreview
@Composable
private fun CoverImageWithoutPicturePreview() = PreviewSurface { CoverImageComponent(artUrl = null, modifier = Modifier.size(52.dp)) }
