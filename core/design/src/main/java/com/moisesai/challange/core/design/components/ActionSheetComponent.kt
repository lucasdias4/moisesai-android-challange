package com.moisesai.challange.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.model.UiGlassState
import com.moisesai.challange.core.design.model.UiGlassStyle
import com.moisesai.challange.core.design.model.rememberUiGlassState
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

private val SheetHeight = 192.dp
private val SheetTopCorner = 16.dp
private val SheetBlur = 50.dp

private val GraberAreaHeight = 14.dp
private val GraberWidth = 56.dp
private val GraberHeight = 5.dp

private val HeaderHeight = 67.dp
private val TitleTop = 10.dp
private val SubtitleTop = 35.85.dp
private val HeaderLineHeight = 26.52.dp
private val SpaceBelowHeader = 14.dp
private val ItemsHorizontalPadding = 24.dp

// The names fade out at both sides, so a long name does not look cut: clear at the edge, solid from 30% to 67%.
private const val NAME_FADE_IN_END = 0.30f
private const val NAME_FADE_OUT_START = 0.67f
private val NamesWidth = 302.dp

/**
 * A sheet that rises from the bottom of the screen over a blur of what is behind it ([glassState]): a handle, the
 * [title] and [subtitle] of what it is about, and the [items] the user can choose.
 */
@Composable
fun ActionSheetComponent(
    title: String,
    subtitle: String,
    glassState: UiGlassState,
    modifier: Modifier = Modifier,
    items: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(topStart = SheetTopCorner, topEnd = SheetTopCorner)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(SheetHeight)
            .glass(glassState, UiGlassStyle(shape = shape, fill = MaterialTheme.design.colors.backgroundAlpha01, blur = SheetBlur)),
    ) {
        Graber()
        Header(title = title, subtitle = subtitle)
        Column(modifier = Modifier.padding(start = ItemsHorizontalPadding, top = SpaceBelowHeader, end = ItemsHorizontalPadding), content = items)
    }
}

@Composable
private fun Graber() {
    Box(modifier = Modifier.fillMaxWidth().height(GraberAreaHeight), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(width = GraberWidth, height = GraberHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.design.colors.alphaInvert25),
        )
    }
}

@Composable
private fun Header(title: String, subtitle: String) {
    val design = MaterialTheme.design
    Box(modifier = Modifier.fillMaxWidth().height(HeaderHeight), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.size(width = NamesWidth, height = HeaderHeight).fadedAtBothSides()) {
            Text(
                text = title,
                modifier = Modifier.offset(y = TitleTop).fillMaxWidth().height(HeaderLineHeight).wrapContentHeight(Alignment.CenterVertically),
                style = design.text.display18,
                color = design.colors.text03,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                modifier = Modifier.offset(y = SubtitleTop).fillMaxWidth().height(HeaderLineHeight).wrapContentHeight(Alignment.CenterVertically),
                style = design.text.text14,
                color = design.colors.text03,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

// A surface of frosted glass: it blurs what is behind it (`glassSource`) and lays a faint fill over it.
private fun Modifier.glass(state: UiGlassState, style: UiGlassStyle): Modifier =
    this
        .clip(style.shape)
        .hazeEffect(state.blur) {
            blurRadius = style.blur
            tints = listOf(HazeTint(style.fill))
            // Before Android 12 there is no blur, so the fill alone is drawn where the blur would be.
            fallbackTint = HazeTint(style.fill)
            // The design's glass is smooth: no grain of its own, the screen has one grain on top of everything.
            noiseFactor = 0f
        }

// Draws the names, then keeps them only where a mask is opaque.
private fun Modifier.fadedAtBothSides(): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    NAME_FADE_IN_END to Color.Black,
                    NAME_FADE_OUT_START to Color.Black,
                    1f to Color.Transparent,
                ),
                blendMode = BlendMode.DstIn,
            )
        }

@FullPreview
@Composable
private fun ActionSheetPreview() = PreviewSurface {
    ActionSheetComponent(title = "Get Lucky", subtitle = "Daft Punk feat. Pharrell Williams", glassState = rememberUiGlassState()) {
        ActionSheetItemComponent(iconRes = DesignR.drawable.ic_setlist, text = "View album", onClick = {})
    }
}
