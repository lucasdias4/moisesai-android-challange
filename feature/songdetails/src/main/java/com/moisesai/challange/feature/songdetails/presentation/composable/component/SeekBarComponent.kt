package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val SeekBarHeight = 24.dp
private val BarLineHeight = 4.dp
private val BarLineCorner = 20.dp

// Where the design puts the line and the handle inside the bar of 24: the line 8 from the top, the handle 2.
private val BarLineTop = 8.dp
private val HandleSize = 16.dp
private val HandleTop = 2.dp

// As in the design, the handle starts 4 before the end of the filled line, so most of it sits after that end.
private val HandleLeftOfFilledBarEnd = 4.dp

// The two ends of the bar: 0 is the start of the song and 1 is its end.
private const val PROGRESS_START = 0f
private const val PROGRESS_END = 1f

/**
 * How far the song has played: a bar that fills from the start up to [progress] (0 to 1), and a round handle.
 * Tapping the bar, or dragging the handle, reports the chosen point to [onProgressChangedByUser] (0 is the start, 1 is
 * the end).
 */
@Composable
internal fun SeekBarComponent(
    progress: Float,
    onProgressChangedByUser: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    // While the finger is down the bar follows it, and not the player, which would answer a moment late.
    var draggedProgress by remember { mutableStateOf<Float?>(null) }
    val shownProgress = draggedProgress ?: progress
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(SeekBarHeight)
            .seekGestures(
                onDragProgress = { draggedProgress = it },
                onProgressChangedByUser = onProgressChangedByUser,
                onDragEnd = { draggedProgress = null },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(current = shownProgress, range = PROGRESS_START..PROGRESS_END)
                setProgress { requestedProgress ->
                    onProgressChangedByUser(requestedProgress.coerceIn(PROGRESS_START, PROGRESS_END))
                    true
                }
            },
    ) {
        val filledBarWidth = maxWidth * shownProgress
        BarLine()
        FilledBarLine(width = filledBarWidth)
        Handle(startOffset = filledBarWidth - HandleLeftOfFilledBarEnd)
    }
}

@Composable
private fun BarLine() {
    Box(
        modifier = Modifier
            .offset(y = BarLineTop)
            .fillMaxWidth()
            .height(BarLineHeight)
            .clip(RoundedCornerShape(BarLineCorner))
            .background(MaterialTheme.design.colors.alphaInvert25),
    )
}

@Composable
private fun FilledBarLine(width: Dp) {
    Box(
        modifier = Modifier
            .offset(y = BarLineTop)
            .width(width)
            .height(BarLineHeight)
            .clip(RoundedCornerShape(BarLineCorner))
            .background(MaterialTheme.design.colors.whiteAlpha60),
    )
}

@Composable
private fun Handle(startOffset: Dp) {
    Box(
        modifier = Modifier
            .offset(x = startOffset, y = HandleTop)
            .size(HandleSize)
            .clip(CircleShape)
            .background(MaterialTheme.design.colors.text03),
    )
}

// A tap reports at once. A drag reports where the finger is while it moves, and reports once more, where it was lifted.
private fun Modifier.seekGestures(
    onDragProgress: (Float) -> Unit,
    onProgressChangedByUser: (Float) -> Unit,
    onDragEnd: () -> Unit,
): Modifier =
    this
        .pointerInput(Unit) {
            detectTapGestures { tapPosition -> onProgressChangedByUser(tapPosition.x.toProgress(size.width)) }
        }
        .pointerInput(Unit) {
            var lastDraggedProgress = PROGRESS_START
            detectHorizontalDragGestures(
                onDragStart = { startPosition ->
                    lastDraggedProgress = startPosition.x.toProgress(size.width).also(onDragProgress)
                },
                onHorizontalDrag = { change, _ ->
                    lastDraggedProgress = change.position.x.toProgress(size.width).also(onDragProgress)
                },
                onDragEnd = {
                    onProgressChangedByUser(lastDraggedProgress)
                    onDragEnd()
                },
                onDragCancel = onDragEnd,
            )
        }

private fun Float.toProgress(widthInPixels: Int): Float = (this / widthInPixels).coerceIn(PROGRESS_START, PROGRESS_END)

@FullPreview
@Composable
private fun SeekBarPreview() = PreviewSurface { SeekBarComponent(progress = 0.25f, onProgressChangedByUser = {}) }
