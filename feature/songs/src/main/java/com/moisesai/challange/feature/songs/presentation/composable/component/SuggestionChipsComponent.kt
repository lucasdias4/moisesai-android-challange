package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.AuroraNight
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.chipPressAnimation
import com.moisesai.challange.feature.songs.presentation.composable.animation.rememberChipPressProgress
import com.moisesai.challange.feature.songs.presentation.composable.component.icon.ChipSearchGlyph
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ContentInset
import com.moisesai.challange.feature.songs.presentation.composable.component.modifier.horizontalEdgeFade
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

// The chips are 40 dp tall in the design. The 4 dp above and below each chip belong to its touch area, which makes
// it the 48 dp that is comfortable to hit, and the space around the row is reduced by the same amount so nothing moves.
private val ChipHeight = 40.dp
private val ChipTouchPadding = 4.dp
private val ChipHorizontalPadding = 14.dp
private val ChipIconGap = 6.dp
private val ChipIconSize = 14.dp
private val ChipSpacing = 8.dp
private val ChipBorderWidth = 1.dp
private val RowTopMargin = 14.dp - ChipTouchPadding

/**
 * Searches the user can start with one tap. The row is clipped at the edge like in the design, and it scrolls
 * sideways to reach the rest.
 */
@Composable
internal fun SuggestionChipsComponent(onSuggestionClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val suggestions = listOf(
        stringResource(R.string.songs_suggestion_levitating),
        stringResource(R.string.songs_suggestion_daft_punk),
        stringResource(R.string.songs_suggestion_bjork),
        stringResource(R.string.songs_suggestion_kendrick_lamar),
    )
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = ContentInset, end = ContentInset, top = RowTopMargin)
            .horizontalEdgeFade(listState)
            .testTag("songs-chips"),
        horizontalArrangement = Arrangement.spacedBy(ChipSpacing),
    ) {
        items(suggestions.size) { index ->
            SuggestionChipComponent(text = suggestions[index], onClick = { onSuggestionClick(suggestions[index]) })
        }
    }
}

@Composable
private fun SuggestionChipComponent(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val press = rememberChipPressProgress(interactionSource)
    val shape = MaterialTheme.shapes.extraLarge
    val colors = MaterialTheme.colorScheme
    val aurora = MaterialTheme.aurora
    Box(
        modifier = Modifier
            .padding(vertical = ChipTouchPadding)
            .chipPressAnimation({ press.value }, shape, glow = colors.primary)
            .height(ChipHeight)
            // The fill and border turn lime as the chip is pressed. Read while drawing, so it does not recompose.
            .drawChipSurface(press = { press.value }, shape = shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val level = press.value.coerceIn(0f, 1f)
        Row(
            modifier = Modifier.padding(horizontal = ChipHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ChipSearchGlyph,
                contentDescription = null,
                // Lime icon and light text turn dark on the lime of a pressed chip.
                tint = lerp(colors.primary, AuroraNight, level),
                modifier = Modifier.size(ChipIconSize),
            )
            Spacer(Modifier.width(ChipIconGap))
            Text(
                text = text,
                style = aurora.text.chip,
                color = lerp(colors.onBackground, AuroraNight, level),
                maxLines = 1,
            )
        }
    }
}

// The chip is a pill: its fill and its border are drawn here so both can turn lime while it is pressed, without
// recomposing. The border is a line inside the edge of the chip, as a CSS border is.
@Composable
private fun Modifier.drawChipSurface(press: () -> Float, shape: Shape): Modifier {
    val aurora = MaterialTheme.aurora.colors
    val lime = MaterialTheme.colorScheme.primary
    val borderWidthPx = with(LocalDensity.current) { ChipBorderWidth.toPx() }
    return this.drawBehind {
        val level = press().coerceIn(0f, 1f)
        val corner = CornerRadius(size.height / 2f)
        drawRoundRect(color = lerp(aurora.chipFill, lime, level), cornerRadius = corner)
        drawRoundRect(
            color = lerp(aurora.chipBorder, lime, level),
            topLeft = Offset(borderWidthPx / 2f, borderWidthPx / 2f),
            size = Size(size.width - borderWidthPx, size.height - borderWidthPx),
            cornerRadius = CornerRadius(corner.x - borderWidthPx / 2f),
            style = Stroke(width = borderWidthPx),
        )
    }
}

@FullPreview
@Composable
private fun SuggestionChipsPreview() = PreviewSurface { SuggestionChipsComponent(onSuggestionClick = {}) }

@FullPreview
@Composable
private fun SuggestionChipPreview() = PreviewSurface { SuggestionChipComponent(text = "Levitating", onClick = {}) }
