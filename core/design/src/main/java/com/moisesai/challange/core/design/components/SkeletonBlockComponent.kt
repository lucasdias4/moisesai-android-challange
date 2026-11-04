package com.moisesai.challange.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.animation.shimmerAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

/** Only tests read this tag: it lets them find the skeleton on screen. It says nothing to a screen reader. */
const val SKELETON_TEST_TAG = "skeleton"

/** A faint block that stands where something is on its way, with a light that sweeps over it. It has no meaning of its own: the screen says what is loading. */
@Composable
fun SkeletonBlockComponent(modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.small) {
    val colors = MaterialTheme.design.colors
    Box(modifier = modifier.testTag(SKELETON_TEST_TAG).clip(shape).background(colors.alphaInvert10).shimmerAnimation(colors.whiteAlpha20))
}

@FullPreview
@Composable
private fun SkeletonBlockPreview() = PreviewSurface { SkeletonBlockComponent(modifier = Modifier.size(width = 200.dp, height = 52.dp)) }
