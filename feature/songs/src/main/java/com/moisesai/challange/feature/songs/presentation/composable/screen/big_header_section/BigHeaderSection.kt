package com.moisesai.challange.feature.songs.presentation.composable.screen.big_header_section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse.rememberSubtitleAlpha
import com.moisesai.challange.feature.songs.presentation.composable.animation.header_collapse.titleCollapseAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.TitleComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ContentInset
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.MinTopSpacing
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.StatusBarClearance
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.TitleSize

private val BigTitleTop = 8.dp
private val SubtitleTop = 8.dp
private val HeaderBottomMargin = 16.dp
private const val SUBTITLE_ALPHA = 0.78f

/** The first thing in the list: the big title and the subtitle, which collapse as the list scrolls. */
@Composable
internal fun BigHeaderSection(collapseProgress: () -> Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(spaceAboveTitle()))
        BigTitle(collapseProgress)
        Subtitle(collapseProgress)
        Spacer(Modifier.height(HeaderBottomMargin))
    }
}

@Composable
private fun BigTitle(collapseProgress: () -> Float) {
    TitleComponent(
        size = TitleSize.Big,
        modifier = Modifier
            .padding(start = ContentInset, top = BigTitleTop)
            .titleCollapseAnimation(collapseProgress)
            .testTag("songs-title"),
    )
}

@Composable
private fun Subtitle(collapseProgress: () -> Float) {
    val subtitleAlpha = rememberSubtitleAlpha(collapseProgress)
    Text(
        text = stringResource(R.string.songs_subtitle),
        style = MaterialTheme.aurora.text.subtitle,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = SUBTITLE_ALPHA),
        modifier = Modifier
            .padding(start = ContentInset, top = SubtitleTop)
            .graphicsLayer { alpha = subtitleAlpha.value }
            .testTag("songs-subtitle"),
    )
}

// At least the minimum, and always clear of the status bar.
@Composable
private fun spaceAboveTitle(): Dp =
    maxOf(MinTopSpacing, WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + StatusBarClearance)

@FullPreview
@Composable
private fun BigHeaderSectionInViewPreview() = PreviewSurface { BigHeaderSection(collapseProgress = { 0f }) }

@FullPreview
@Composable
private fun BigHeaderSectionHalfCollapsedPreview() = PreviewSurface { BigHeaderSection(collapseProgress = { 0.5f }) }
