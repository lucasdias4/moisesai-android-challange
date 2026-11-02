package com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moisesai.challange.feature.songs.presentation.composable.animation.reveal.RevealTracker
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.ScreenHorizontalPadding
import com.moisesai.challange.feature.songs.presentation.composable.model.ListContext
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions

/** Says every item has already arrived, so a preview shows the items in place and not in the middle of their arrival. */
internal class AllRevealedTracker : RevealTracker() {
    override fun hasRevealed(key: Any): Boolean = true
}

/** What a section needs to be drawn in a preview: no actions, fully visible, already in place. */
internal fun previewListContext() = ListContext(SongsActions.None, Modifier, AllRevealedTracker())

/** A song list around the items [content] adds, so a preview of one section shows it as the app does. */
@Composable
internal fun SongsListPreviewSurface(content: LazyListScope.(ListContext) -> Unit) {
    val context = previewListContext()
    PreviewSurface {
        LazyColumn(contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding)) { content(context) }
    }
}
