package com.moisesai.challange.core.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

// The bar is this part of the line's height, so a bar looks like a line of text and keeps the air between two lines.
private const val BAR_HEIGHT_PART = 0.7f

/**
 * A bar that stands where a line of text written in [style] will be. Its line is as high as the real text, so the
 * layout does not move when the text arrives. The caller sets its width.
 */
@Composable
fun SkeletonTextComponent(style: TextStyle, modifier: Modifier = Modifier) {
    val lineHeight = with(LocalDensity.current) { style.lineHeight.toDp() }
    Box(modifier = modifier.height(lineHeight), contentAlignment = Alignment.CenterStart) {
        SkeletonBlockComponent(modifier = Modifier.fillMaxWidth().height(lineHeight * BAR_HEIGHT_PART))
    }
}

@FullPreview
@Composable
private fun SkeletonTextPreview() = PreviewSurface {
    SkeletonTextComponent(style = MaterialTheme.design.text.text16, modifier = Modifier.fillMaxWidth(0.6f))
}
