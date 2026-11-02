package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface

private val MessageTopPadding = 48.dp
private val MessageHorizontalPadding = 24.dp
private val SpaceBetweenMessageParts = 8.dp

/** What the list says when there are no songs to show: a [title], a [message] and, if there is one, an [action]. */
@Composable
internal fun MessageStateComponent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val design = MaterialTheme.design
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MessageTopPadding, start = MessageHorizontalPadding, end = MessageHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceBetweenMessageParts),
    ) {
        Text(text = title, style = design.text.display16, color = design.colors.text03, textAlign = TextAlign.Center)
        Text(text = message, style = design.text.text14, color = design.colors.text01, textAlign = TextAlign.Center)
        action?.invoke()
    }
}

@FullPreview
@Composable
private fun MessageStatePreview() = PreviewSurface {
    MessageStateComponent(title = "No songs found", message = "Nothing matches “zzzz”. Try another search.")
}
