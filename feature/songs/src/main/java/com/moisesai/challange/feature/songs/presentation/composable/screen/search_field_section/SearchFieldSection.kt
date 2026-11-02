package com.moisesai.challange.feature.songs.presentation.composable.screen.search_field_section

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.SearchFieldComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions

private val FieldHorizontalMargin = 20.dp
private val FieldVerticalMargin = 8.dp

/** The search field with the space the design leaves around it. */
@Composable
internal fun SearchFieldSection(query: String, actions: SongsActions, modifier: Modifier = Modifier) {
    SearchFieldComponent(
        query = query,
        onQueryChange = actions.onQueryChange,
        onSearchSubmit = actions.onSearchSubmit,
        modifier = modifier.padding(horizontal = FieldHorizontalMargin, vertical = FieldVerticalMargin),
    )
}

@FullPreview
@Composable
private fun SearchFieldSectionPreview() = PreviewSurface { SearchFieldSection(query = "", actions = SongsActions.None) }
