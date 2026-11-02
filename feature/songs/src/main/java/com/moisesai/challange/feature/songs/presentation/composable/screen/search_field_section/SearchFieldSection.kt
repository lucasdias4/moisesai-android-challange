package com.moisesai.challange.feature.songs.presentation.composable.screen.search_field_section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.feature.songs.presentation.composable.component.SearchFieldComponent
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ScreenHorizontalPadding
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.SearchFieldHeight
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.StickyFieldTop
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.ScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.composable.model.rememberScrollContext
import com.moisesai.challange.feature.songs.presentation.composable.screen.songs_list_section.FIELD_SLOT_KEY
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState

private const val FIELD_SLOT_SCROLLED_AWAY = Int.MIN_VALUE

// The real search field is not part of the list: it sits where its slot is and stays at the top once the list has
// scrolled past it. Its position is read while placing it, so scrolling never recomposes the screen.
@Composable
internal fun SearchFieldSection(uiState: UiSongsUiState, actions: SongsActions, scroll: ScrollContext) {
    val stickyTopPx = with(LocalDensity.current) { StickyFieldTop.roundToPx() }
    SearchFieldComponent(
        uiState = uiState,
        actions = actions,
        glassState = scroll.glassState,
        modifier = Modifier
            .padding(horizontal = ScreenHorizontalPadding)
            .offset { IntOffset(0, maxOf(stickyTopPx, scroll.listState.fieldSlotTop())) },
    )
}

// Where the field's slot is in the list, or far above the screen once the slot has scrolled out of the list.
private fun LazyListState.fieldSlotTop(): Int =
    layoutInfo.visibleItemsInfo.firstOrNull { it.key == FIELD_SLOT_KEY }?.offset ?: FIELD_SLOT_SCROLLED_AWAY

// With no list to scroll, the field sits at its resting place near the top, as it does once the list has scrolled.
// That place is below the top edge, so the preview needs the room for it: the field is only moved, not given space.
@Composable
private fun SearchFieldSectionPreviewOf(uiState: UiSongsUiState) = PreviewSurface {
    Box(modifier = Modifier.fillMaxWidth().height(StickyFieldTop + SearchFieldHeight)) {
        SearchFieldSection(uiState, SongsActions.None, rememberScrollContext())
    }
}

private val FirstLaunchSample = UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable)

@FullPreview
@Composable
private fun SearchFieldSectionEmptyPreview() =
    SearchFieldSectionPreviewOf(UiSongsUiState(content = FirstLaunchSample))

@FullPreview
@Composable
private fun SearchFieldSectionTypedPreview() =
    SearchFieldSectionPreviewOf(UiSongsUiState(query = "Levitating", content = FirstLaunchSample))

