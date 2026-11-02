package com.moisesai.challange.feature.songs.presentation.composable.screen.compact_header_section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.GlassState
import com.moisesai.challange.core.design.components.GlassStyle
import com.moisesai.challange.core.design.components.glass
import com.moisesai.challange.core.design.components.rememberGlassState
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse.compactHeaderRevealAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.TitleComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.model.TitleSize

// This takes the place of the big header (`BigHeaderSection`) once the list has scrolled past it: it fades in as the
// big title shrinks and fades out, over the first 110 dp of scroll, and then stays fixed at the top of the screen.
private val CompactHeaderHeight = 100.dp
private val CompactTitleTop = 50.dp
private val CompactTitleStart = 22.dp
private val CompactHeaderBlur = 14.dp
private const val COMPACT_HEADER_FILL_ALPHA = 0.7f

// The veil and the title stay fully visible down to this share of the header, then fade away towards the bottom.
private const val COMPACT_HEADER_FADE_START = 0.7f
private const val FULLY_TRANSPARENT = 0f

// The small title at the top, over a dark veil that fades away downwards. It appears as the big one leaves.
@Composable
internal fun CompactHeaderSection(scroll: ScrollContext) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CompactHeaderHeight)
            .compactHeaderRevealAnimation(scroll.collapseProgress)
            .fadeOutTowardsBottom(),
    ) {
        GlassVeil(scroll.glassState)
        TitleComponent(
            size = TitleSize.Compact,
            modifier = Modifier.padding(start = CompactTitleStart, top = CompactTitleTop),
        )
    }
}

// Blurs what scrolls under the header, tinted with the background so the compact title stays readable.
@Composable
private fun GlassVeil(glassState: GlassState) {
    val background = MaterialTheme.colorScheme.background
    val tint = Brush.verticalGradient(
        listOf(background.copy(alpha = COMPACT_HEADER_FILL_ALPHA), background.copy(alpha = FULLY_TRANSPARENT)),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .glass(glassState, GlassStyle(RectangleShape, Color.Transparent, blur = CompactHeaderBlur))
            .background(tint),
    )
}

// The veil and the title are faded away together, which needs them drawn off screen first.
private fun Modifier.fadeOutTowardsBottom(): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Black,
                    COMPACT_HEADER_FADE_START to Color.Black,
                    1f to Color.Black.copy(alpha = FULLY_TRANSPARENT),
                ),
                // Keeps the content only where the mask is opaque.
                blendMode = BlendMode.DstIn,
            )
        }

// The compact header only shows once the list has scrolled, so the preview pretends it fully has.
@FullPreview
@Composable
private fun CompactHeaderSectionPreview() = PreviewSurface {
    val scroll = ScrollContext(rememberLazyListState(), rememberGlassState()) { 1f }
    CompactHeaderSection(scroll)
}

