package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

/** Shown when a search worked and found nothing. */
@Composable
internal fun NoResultsStateComponent(query: String, modifier: Modifier = Modifier) {
    StateCardComponent(
        title = stringResource(R.string.songs_no_results_title),
        message = stringResource(R.string.songs_no_results_message, query),
        modifier = modifier,
    )
}

@FullPreview
@Composable
private fun NoResultsStatePreview() = PreviewSurface { NoResultsStateComponent(query = "asfasgs") }
