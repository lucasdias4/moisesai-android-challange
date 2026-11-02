package com.moisesai.challange.core.design.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme

private val PreviewPadding = 16.dp

/** The theme and the screen's background around a component, so a preview shows it as the app does. */
@Composable
fun PreviewSurface(content: @Composable () -> Unit) {
    MoisesaiChallangeTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(PreviewPadding)) { content() }
    }
}
