package com.moisesai.challange.feature.songs.presentation.composable.screen.header_section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.animation.PopAnimation
import com.moisesai.challange.core.design.components.IconButtonComponent
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.ScreenTitleComponent

// The design leaves this much between the status bar and the title.
private val SpaceBelowStatusBar = 20.dp

/**
 * The top of the screen: room for the status bar, then the name of the screen. The magnifier at the end pops in while
 * [isSearchButtonVisible], which is when the search field is tucked away.
 */
@Composable
internal fun HeaderSection(title: String, isSearchButtonVisible: Boolean, onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(statusBarHeight + SpaceBelowStatusBar))
        ScreenTitleComponent(
            title = title,
            // The search magnifier pops in (it grows while it fades in) when the search field is tucked away, and pops out when the field returns.
            endAction = { PopAnimation(isVisible = isSearchButtonVisible) { SearchButton(onClick = onSearchClick) } },
        )
    }
}

@Composable
private fun SearchButton(onClick: () -> Unit) {
    IconButtonComponent(
        iconRes = DesignR.drawable.ic_search,
        contentDescription = stringResource(R.string.songs_search_button),
        onClick = onClick,
    )
}

@FullPreview
@Composable
private fun HeaderSectionPreview() = PreviewSurface { HeaderSection(title = "Songs", isSearchButtonVisible = false, onSearchClick = {}) }

@FullPreview
@Composable
private fun HeaderSectionWithSearchPreview() = PreviewSurface {
    HeaderSection(title = "Songs", isSearchButtonVisible = true, onSearchClick = {})
}
