package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError

/** Shown when the first page could not be loaded and there is nothing else to show. */
@Composable
internal fun ErrorStateComponent(error: UiSongsError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    StateCardComponent(
        title = stringResource(error.titleRes()),
        message = stringResource(error.messageRes()),
        modifier = modifier,
    ) {
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(text = stringResource(R.string.songs_retry), style = MaterialTheme.aurora.text.chip)
        }
    }
}

@StringRes
private fun UiSongsError.titleRes() = when (this) {
    UiSongsError.Network -> R.string.songs_error_network_title
    UiSongsError.Unknown -> R.string.songs_error_unknown_title
}

@StringRes
private fun UiSongsError.messageRes() = when (this) {
    UiSongsError.Network -> R.string.songs_error_network_message
    UiSongsError.Unknown -> R.string.songs_error_unknown_message
}

@FullPreview
@Composable
private fun ErrorStateNetworkPreview() = PreviewSurface { ErrorStateComponent(error = UiSongsError.Network, onRetry = {}) }

@FullPreview
@Composable
private fun ErrorStateUnknownPreview() = PreviewSurface { ErrorStateComponent(error = UiSongsError.Unknown, onRetry = {}) }
