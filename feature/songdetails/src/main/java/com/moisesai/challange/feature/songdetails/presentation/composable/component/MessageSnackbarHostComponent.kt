package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val MessageCorner = 12.dp
private val MessageHorizontalMargin = 24.dp
private val MessageVerticalMargin = 12.dp

/**
 * Where the short messages of the screen appear, in the colours of the design. The screen asks [hostState] to show a
 * message; it stays a few seconds and is read aloud by screen readers.
 */
@Composable
internal fun MessageSnackbarHostComponent(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    // The message fades and slides in and out by itself (the snackbar animation of Material), and leaves after a few seconds.
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(horizontal = MessageHorizontalMargin, vertical = MessageVerticalMargin),
    ) { snackbarData -> MessageSnackbar(message = snackbarData.visuals.message) }
}

@Composable
private fun MessageSnackbar(message: String) {
    val design = MaterialTheme.design
    Snackbar(
        shape = RoundedCornerShape(MessageCorner),
        containerColor = design.colors.background03,
        contentColor = design.colors.text03,
    ) {
        Text(text = message, style = design.text.text14)
    }
}

@FullPreview
@Composable
private fun MessageSnackbarPreview() = PreviewSurface { MessageSnackbar(message = "Can't play this song. Check your internet connection.") }
