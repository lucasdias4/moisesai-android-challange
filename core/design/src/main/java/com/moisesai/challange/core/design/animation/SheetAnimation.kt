package com.moisesai.challange.core.design.animation

import com.moisesai.challange.core.design.animation.spec.screenTween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import com.moisesai.challange.core.design.theme.design
import kotlinx.coroutines.launch

// How dark the screen gets behind the open sheet: enough to push it back, little enough to still see what is there.
private const val SCRIM_ALPHA = 0.4f

// Pulling the sheet down past this part of its height closes it when the finger lets go.
private const val DISMISS_DISTANCE_PART = 0.3f

// A quick flick closes it even if it did not go far. In pixels per second.
private const val DISMISS_VELOCITY = 1_500f

// How long the sheet takes to leave after a pull. Shorter than a screen change, because the finger already moved it.
private const val PULL_EXIT_MILLIS = 180

/**
 * Rises a sheet from the bottom over a fading scrim, slides it down to close, and lets the user pull it down or tap the scrim to dismiss it.
 * Used in `SongOptionsSection`: `SheetAnimation(isVisible = trackOptions is Visible, onDismiss = ...) { ActionSheetComponent(...) }`.
 */
@Composable
fun SheetAnimation(isVisible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier, sheet: @Composable () -> Unit) {
    // How far the finger pulled the sheet down: 0 = in place.
    val pulledDistance = remember { Animatable(0f) }
    var sheetHeight by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    // A sheet that was pulled away must come back in place the next time it opens.
    LaunchedEffect(isVisible) { if (isVisible) pulledDistance.snapTo(0f) }

    // The sheet is only composed while it is visible or leaving, so the rest of the screen stays touchable when it is not.
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = isVisible, enter = fadeIn(screenTween()), exit = fadeOut(screenTween())) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.design.colors.background00.copy(alpha = SCRIM_ALPHA))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
            )
        }
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(animationSpec = screenTween()) { fullHeight -> fullHeight },
            exit = slideOutVertically(animationSpec = screenTween()) { fullHeight -> fullHeight },
        ) {
            Box(
                modifier = Modifier
                    .onSizeChanged { size -> sheetHeight = size.height }
                    .graphicsLayer { translationY = pulledDistance.value }
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            // The sheet follows the finger down, but never up past its place.
                            scope.launch { pulledDistance.snapTo((pulledDistance.value + delta).coerceAtLeast(0f)) }
                        },
                        onDragStopped = { velocity ->
                            val wasPulledFarEnough = pulledDistance.value > sheetHeight * DISMISS_DISTANCE_PART
                            val wasFlicked = velocity > DISMISS_VELOCITY
                            if (wasPulledFarEnough || wasFlicked) {
                                pulledDistance.animateTo(sheetHeight.toFloat(), tween(PULL_EXIT_MILLIS))
                                onDismiss()
                            } else {
                                pulledDistance.animateTo(0f)
                            }
                        },
                    ),
            ) {
                sheet()
            }
        }
    }
}
