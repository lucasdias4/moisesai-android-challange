package com.moisesai.challange.core.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R

private val LoaderVerticalPadding = 32.dp
private val LoaderSize = 32.dp
private val LoaderLineWidth = 2.dp

/** A round loader in the middle of the width: what the screen needs is on its way. */
@Composable
fun LoadingIndicatorComponent(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.common_loading)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LoaderVerticalPadding)
            .semantics { contentDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(LoaderSize),
            color = MaterialTheme.design.colors.element07,
            strokeWidth = LoaderLineWidth,
        )
    }
}

@FullPreview
@Composable
private fun LoadingIndicatorPreview() = PreviewSurface { LoadingIndicatorComponent() }
