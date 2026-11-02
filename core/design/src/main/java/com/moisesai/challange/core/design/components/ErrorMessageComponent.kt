package com.moisesai.challange.core.design.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R

private val MessageTopPadding = 48.dp
private val MessageHorizontalPadding = 24.dp
private val SpaceBetweenMessageParts = 8.dp

/** Shown when a screen failed to load and has nothing else to show: what went wrong, and a button to try again. */
@Composable
fun ErrorMessageComponent(type: UiErrorType, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MessageTopPadding, start = MessageHorizontalPadding, end = MessageHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceBetweenMessageParts),
    ) {
        Text(text = stringResource(type.titleRes()), style = design.text.display16, color = design.colors.text03, textAlign = TextAlign.Center)
        Text(text = stringResource(type.messageRes()), style = design.text.text14, color = design.colors.text01, textAlign = TextAlign.Center)
        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = design.colors.element07, contentColor = design.colors.element00),
        ) {
            Text(text = stringResource(R.string.common_retry), style = design.text.display14)
        }
    }
}

@StringRes
private fun UiErrorType.titleRes() = when (this) {
    UiErrorType.Network -> R.string.common_error_network_title
    UiErrorType.Unknown -> R.string.common_error_unknown_title
}

@StringRes
private fun UiErrorType.messageRes() = when (this) {
    UiErrorType.Network -> R.string.common_error_network_message
    UiErrorType.Unknown -> R.string.common_error_unknown_message
}

@FullPreview
@Composable
private fun ErrorMessageNetworkPreview() = PreviewSurface { ErrorMessageComponent(type = UiErrorType.Network, onRetry = {}) }

@FullPreview
@Composable
private fun ErrorMessageUnknownPreview() = PreviewSurface { ErrorMessageComponent(type = UiErrorType.Unknown, onRetry = {}) }
