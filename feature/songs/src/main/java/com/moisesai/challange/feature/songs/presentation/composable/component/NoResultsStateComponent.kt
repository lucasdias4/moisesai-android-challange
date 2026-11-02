package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.strings.R

/** Shown when a search worked and found nothing for [searchText]. */
@Composable
internal fun NoResultsStateComponent(searchText: String, modifier: Modifier = Modifier) {
    MessageStateComponent(
        title = stringResource(R.string.songs_no_results_title),
        message = stringResource(R.string.songs_no_results_message, searchText),
        modifier = modifier,
    )
}

@FullPreview
@Composable
private fun NoResultsStatePreview() = PreviewSurface { NoResultsStateComponent(searchText = "asfasgs") }
