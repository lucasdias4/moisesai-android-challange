package com.moisesai.challange.core.design.extension

import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.model.UiGlassState
import dev.chrisbanes.haze.hazeSource

/** Marks this content as what the glass surfaces drawn over it blur. */
fun Modifier.glassSource(state: UiGlassState): Modifier = hazeSource(state.blur)
