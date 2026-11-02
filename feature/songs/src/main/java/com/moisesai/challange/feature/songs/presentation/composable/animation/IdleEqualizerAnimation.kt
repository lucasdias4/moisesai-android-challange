package com.moisesai.challange.feature.songs.presentation.composable.animation

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.moisesai.challange.core.design.animation.CssEaseInOutEasing
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.AuroraLime
import com.moisesai.challange.core.design.theme.AuroraMagenta
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import kotlin.math.roundToInt
import kotlin.math.sin

private const val BAR_COUNT = 14
private val BarWidth = 8.dp
private val BarGap = 5.dp
private val EqualizerHeight = 34.dp

// A resting bar is as tall as it is wide (a dot); every bar has its own highest point, between 10 and 34 dp, so the row
// looks like a sound wave and not like a comb. The sine spreads the heights smoothly along the row.
private val RestingBarHeight = 8.dp
private const val MIN_PEAK_DP = 10f
private const val PEAK_RANGE_DP = 24f
private const val PEAK_SINE_STEP = 1.3

// One way up takes 1.9 seconds, then the bar comes back down in the same time.
private const val BOUNCE_MILLIS = 1_900

// Each bar is this much further into its cycle than the one before it, so the bars never move in step.
private const val BAR_PHASE_STEP_MILLIS = 170

/** A row of bars that rise and fall, the motif of the splash. It says "waiting" without saying what for. */
@Composable
internal fun IdleEqualizerAnimation(modifier: Modifier = Modifier) {
    val bounceFractions = rememberBounceFractions()
    Canvas(modifier = modifier.width(BarWidth * BAR_COUNT + BarGap * (BAR_COUNT - 1)).height(EqualizerHeight)) {
        drawBars(bounceFractions)
    }
}

@Composable
private fun rememberBounceFractions(): List<State<Float>> {
    val transition = rememberInfiniteTransition(label = "idleBounce")
    // The number of bars never changes, so calling an animation once per bar is safe here.
    return List(BAR_COUNT) { index -> transition.bounceFraction(index) }
}

@Composable
private fun InfiniteTransition.bounceFraction(barIndex: Int): State<Float> =
    animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = BOUNCE_MILLIS, easing = CssEaseInOutEasing),
            // Reverse makes the bar go up and then down, forever.
            repeatMode = RepeatMode.Reverse,
            // Fast-forwarding starts each bar part of the way through its first rise, like a negative CSS delay.
            initialStartOffset = StartOffset(barIndex * BAR_PHASE_STEP_MILLIS, StartOffsetType.FastForward),
        ),
        label = "idleBar$barIndex",
    )

private fun DrawScope.drawBars(bounceFractions: List<State<Float>>) {
    val barWidth = BarWidth.toPx()
    val barStep = barWidth + BarGap.toPx()
    bounceFractions.forEachIndexed { index, bounce ->
        val peak = peakHeightDp(index).dp.toPx()
        val barHeight = lerp(RestingBarHeight.toPx(), peak, bounce.value)
        // Bars grow up and down together, around the middle of the row.
        val top = (size.height - barHeight) / 2f
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(AuroraLime, AuroraMagenta),
                startY = top,
                endY = top + barHeight,
            ),
            topLeft = Offset(index * barStep, top),
            size = Size(barWidth, barHeight),
            // Half the width makes the ends fully round.
            cornerRadius = CornerRadius(barWidth / 2f),
        )
    }
}

private fun peakHeightDp(barIndex: Int): Float =
    MIN_PEAK_DP + ((sin(barIndex * PEAK_SINE_STEP) + 1.0) * PEAK_RANGE_DP / 2.0).roundToInt()

@FullPreview
@Composable
private fun IdleEqualizerAnimationPreview() {
    MoisesaiChallangeTheme {
        IdleEqualizerAnimation()
    }
}
