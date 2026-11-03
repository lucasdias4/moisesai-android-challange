package com.moisesai.challange.feature.album.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val SpaceBetweenNames = 8.dp

/** The name of the album and, under it, the name of the artist, both centred. */
@Composable
internal fun AlbumNamesComponent(albumName: String, artistName: String, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceBetweenNames),
    ) {
        Text(text = albumName, style = design.text.display20, color = design.colors.text03, textAlign = TextAlign.Center)
        Text(text = artistName, style = design.text.text14, color = design.colors.text03, textAlign = TextAlign.Center)
    }
}

@FullPreview
@Composable
private fun AlbumNamesPreview() = PreviewSurface { AlbumNamesComponent(albumName = "Discovery", artistName = "Daft Punk") }
