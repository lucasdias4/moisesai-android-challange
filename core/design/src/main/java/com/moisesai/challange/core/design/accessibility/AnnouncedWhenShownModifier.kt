package com.moisesai.challange.core.design.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

/**
 * Accessibility (`semantics { liveRegion }`, polite): makes a screen reader read the element out when it appears or its text changes, without moving the user's focus.
 * Used on messages that replace the content of a screen (an error, "no songs found"): a sighted user sees the change, so
 * a user who cannot see must be told. It waits for the speech in progress to end (polite) so it never cuts the user off.
 */
fun Modifier.announcedWhenShown(): Modifier = semantics { liveRegion = LiveRegionMode.Polite }
