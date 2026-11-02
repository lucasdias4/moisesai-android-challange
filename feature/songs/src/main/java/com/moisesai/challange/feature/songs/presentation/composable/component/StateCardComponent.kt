package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.glassEdge
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val StateSpacing = 12.dp
private val StatePadding = 24.dp
// The card starts clear of the search field above it, like the section label that precedes the results.
private val StateCardTopSpacing = 16.dp

@Composable
internal fun StateCardComponent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
) {
    val aurora = MaterialTheme.aurora
    Column(
        modifier = modifier
            .padding(top = StateCardTopSpacing)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(aurora.colors.glassFill)
            .glassEdge(MaterialTheme.shapes.large)
            .padding(StatePadding),
        verticalArrangement = Arrangement.spacedBy(StateSpacing),
    ) {
        Text(text = title, style = aurora.text.ghostTitle, color = MaterialTheme.colorScheme.onBackground)
        Text(text = message, style = aurora.text.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        action()
    }
}

@FullPreview
@Composable
private fun StateCardPreview() = PreviewSurface {
    StateCardComponent(title = "Something to say", message = "A card with a title and a message, and nothing else.")
}
