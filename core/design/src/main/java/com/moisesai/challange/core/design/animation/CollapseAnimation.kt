package com.moisesai.challange.core.design.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Opens its content down from nothing while it fades in, and closes it up while it fades out.
 * Used in `SongsScreen`, around the search field: `CollapseAnimation(isVisible = uiState.isSearchFieldVisible) { SearchFieldSection(...) }`.
 */
@Composable
fun CollapseAnimation(isVisible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = expandVertically(screenTween()) + fadeIn(screenTween()),
        exit = shrinkVertically(screenTween()) + fadeOut(screenTween()),
    ) { content() }
}
