package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ContentInset
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val StateSpacing = 12.dp

/** Shown above results that are still on screen when an update failed. */
@Composable
internal fun PartialErrorMessageComponent(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.songs_partial_error),
        style = MaterialTheme.aurora.text.caption,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ContentInset, vertical = StateSpacing)
            .semantics { liveRegion = LiveRegionMode.Polite },
    )
}

@FullPreview
@Composable
private fun PartialErrorMessagePreview() = PreviewSurface { PartialErrorMessageComponent() }
