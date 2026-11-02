package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ContentInset
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val LabelTopPadding = 22.dp
private val LabelBottomPadding = 12.dp
private val LabelToLineGap = 10.dp
private val LineThickness = 1.dp

/** The small uppercase name of a section, followed by a hairline that fades away. Screen readers treat it as a heading. */
@Composable
internal fun SectionLabelComponent(text: String, modifier: Modifier = Modifier, topPadding: Dp = LabelTopPadding) {
    val aurora = MaterialTheme.aurora
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = ContentInset, end = ContentInset, top = topPadding, bottom = LabelBottomPadding)
            .semantics { heading() }
            .testTag("songs-section-label"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            style = aurora.text.sectionLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .padding(start = LabelToLineGap)
                .weight(1f)
                .height(LineThickness)
                .background(Brush.horizontalGradient(listOf(aurora.colors.labelLine, Color.White.copy(alpha = 0f)))),
        )
    }
}

@FullPreview
@Composable
private fun SectionLabelPreview() = PreviewSurface { SectionLabelComponent(text = "START HERE") }
