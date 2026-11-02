package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val MessageVerticalPadding = 8.dp

/** Said above the songs when an update failed but the songs from before are still shown. */
@Composable
internal fun PartialErrorMessageComponent(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.songs_partial_error),
        modifier = modifier.fillMaxWidth().padding(vertical = MessageVerticalPadding),
        style = MaterialTheme.design.text.text12,
        color = MaterialTheme.design.colors.text01,
    )
}

@FullPreview
@Composable
private fun PartialErrorMessagePreview() = PreviewSurface { PartialErrorMessageComponent() }
