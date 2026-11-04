package com.moisesai.challange.feature.songs.presentation.composable.screen.search_field_section

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.callback.SongsCallbacks
import com.moisesai.challange.feature.songs.presentation.composable.component.SearchFieldComponent

private val FieldHorizontalMargin = 20.dp
private val FieldVerticalMargin = 8.dp

/** The search field with the space the design leaves around it. */
@Composable
internal fun SearchFieldSection(query: String, callbacks: SongsCallbacks, modifier: Modifier = Modifier) {
    SearchFieldComponent(
        query = query,
        onQueryChange = callbacks.onQueryChange,
        onSearchSubmit = callbacks.onSearchSubmit,
        modifier = modifier.padding(horizontal = FieldHorizontalMargin, vertical = FieldVerticalMargin),
    )
}

@FullPreview
@Composable
private fun SearchFieldSectionPreview() = PreviewSurface { SearchFieldSection(query = "", callbacks = SongsCallbacks.None) }
