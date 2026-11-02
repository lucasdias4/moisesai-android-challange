package com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PreviewListHorizontalPadding = 24.dp

/** A list around the items [content] adds, so a preview of one part of the songs list shows it as the app does. */
@Composable
internal fun SongsListPreviewSurface(content: LazyListScope.() -> Unit) {
    PreviewSurface {
        LazyColumn(contentPadding = PaddingValues(horizontal = PreviewListHorizontalPadding), content = content)
    }
}
