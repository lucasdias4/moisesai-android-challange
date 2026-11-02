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
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val TitleRowHeight = 48.dp
private val TitleRowHorizontalPadding = 24.dp

/** The name of the screen, in a row 48 high, centred up and down. */
@Composable
internal fun ScreenTitleComponent(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(TitleRowHeight).padding(horizontal = TitleRowHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.design.text.display24, color = MaterialTheme.design.colors.text03)
    }
}

@FullPreview
@Composable
private fun ScreenTitlePreview() = PreviewSurface { ScreenTitleComponent(title = "Songs") }
