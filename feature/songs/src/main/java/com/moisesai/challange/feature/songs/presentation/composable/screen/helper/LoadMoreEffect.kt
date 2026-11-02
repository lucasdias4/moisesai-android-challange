package com.moisesai.challange.feature.songs.presentation.composable.screen.helper

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

// The next page is asked for when the user is this many rows from the end, so it is ready before the end is reached.
private const val LOAD_MORE_THRESHOLD = 5

// Asks for the next page when the user nears the end of the list, and again if the list grew and is still near the end.
@Composable
internal fun LoadMoreEffect(listState: LazyListState, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isNearEnd() to listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .filter { (isNearEnd, _) -> isNearEnd }
            .collect { currentOnLoadMore() }
    }
}

private fun LazyListState.isNearEnd(): Boolean {
    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return false
    return lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
}
