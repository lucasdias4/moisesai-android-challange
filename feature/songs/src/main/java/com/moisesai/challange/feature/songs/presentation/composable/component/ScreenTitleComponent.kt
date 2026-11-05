package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val TitleRowHeight = 48.dp
private val TitleRowHorizontalPadding = 24.dp

/** The name of the screen, in a row 48 high, centred up and down, with a button at the end ([endAction]) if there is one. */
@Composable
internal fun ScreenTitleComponent(title: String, modifier: Modifier = Modifier, endAction: (@Composable () -> Unit)? = null) {
    Row(
        modifier = modifier.fillMaxWidth().height(TitleRowHeight).padding(horizontal = TitleRowHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            // Accessibility (`semantics { heading() }`): a screen reader treats the screen name as a heading, so the user can jump to it.
            modifier = Modifier.weight(1f).semantics { heading() },
            style = MaterialTheme.design.text.display24,
            color = MaterialTheme.design.colors.text03,
        )
        endAction?.invoke()
    }
}

@FullPreview
@Composable
private fun ScreenTitlePreview() = PreviewSurface { ScreenTitleComponent(title = "Songs") }
