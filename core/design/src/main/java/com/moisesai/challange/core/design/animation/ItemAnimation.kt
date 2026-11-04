package com.moisesai.challange.core.design.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.ui.Modifier

/**
 * Fades a list item in and out, and glides it to its new place when the items around it change (the list needs a key per item).
 * Used in `songRowListComponent`, on each row: `SongRowComponent(modifier = itemAnimation(), track = track, ...)`.
 */
fun LazyItemScope.itemAnimation(): Modifier = Modifier.animateItem(fadeInSpec = screenTween(), fadeOutSpec = screenTween())
