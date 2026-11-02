package com.moisesai.challange.feature.songs.presentation.composable.component.layout

import androidx.compose.ui.unit.dp

// The measurements of the screen, from the agreed design (360 wide, one dp is one pixel of it).

/** Space left and right of everything in the list. */
internal val ScreenHorizontalPadding = 16.dp

/** Text and chips are inset a little more than cards and rows, which fill the whole width. */
internal val ContentInset = 6.dp

/** The search field is a 58 dp tall pill. */
internal val SearchFieldHeight = 58.dp

/** Above the title: at least this much, and always clear of the status bar. */
internal val MinTopSpacing = 48.dp
internal val StatusBarClearance = 16.dp

/** Under the last item: room for what will float above the list later (the mini-player). */
internal val BottomSpacing = 170.dp

/** The search field stays this far from the top of the screen when the list has scrolled past it. */
internal val StickyFieldTop = 94.dp

/** Space under a card, and between cards. */
internal val CardSpacing = 14.dp
