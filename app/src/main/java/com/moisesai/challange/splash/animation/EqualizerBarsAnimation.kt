package com.moisesai.challange.splash.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

private const val BAR_COUNT = 14
// Lowest bounce height (share of the area), so a bar never disappears.
private const val MIN_BAR_FRACTION = 0.08f
// Bars bounce at 4 different speeds (420 to 750 ms), so the row looks random and not in sync.
private const val BASE_BOUNCE_MILLIS = 420
private const val BOUNCE_MILLIS_STEP = 110
private const val BOUNCE_MILLIS_VARIANTS = 4
// Each bar starts its bounce a bit further along than the previous one.
private const val START_OFFSET_STEP_MILLIS = 70
// The gap between two bars is 60% of a bar's width.
private const val BAR_GAP_TO_WIDTH_RATIO = 0.6f
// How fast the bars fade out when the wordmark appears.
private const val FADE_OUT_MILLIS = 400

private val BarsAreaHeight = 240.dp
private val BarsAreaHorizontalPadding = 40.dp

/**
 * A row of rounded bars, centred vertically. They bounce while [phase] is
 * [SplashPhase.Equalizing], shrink to a row of dots from [SplashPhase.Collapsing]
 * and fade away once the wordmark is revealed.
 */
@Composable
internal fun EqualizerBarsAnimation(
    phase: SplashPhase,
    modifier: Modifier = Modifier,
) {
    // One endless bounce value per bar (0 to 1, a share of the area height).
    val bounceFractions = rememberBounceFractions()
    // 0 = bars bouncing, 1 = bars collapsed into dots.
    val collapseFraction by animateFloatAsState(
        targetValue = if (phase >= SplashPhase.Collapsing) 1f else 0f,
        animationSpec = tween(SplashPhase.Collapsing.durationMillis.toInt()),
        label = "collapseFraction",
    )
    // 1 = visible, 0 = gone. Bars leave when the wordmark arrives.
    val fadeOut by animateFloatAsState(
        targetValue = if (phase >= SplashPhase.Revealing) 0f else 1f,
        animationSpec = tween(FADE_OUT_MILLIS),
        label = "barsAlpha",
    )
    val brush = rememberBarBrush()

    // All bars are drawn on one Canvas. The values are read inside the draw block,
    // so every animation frame only redraws and never recomposes.
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BarsAreaHorizontalPadding)
            .height(BarsAreaHeight)
            // graphicsLayer applies the fade without recomposing.
            .graphicsLayer { alpha = fadeOut },
    ) {
        drawBars(bounceFractions, collapseFraction, brush)
    }
}

@Composable
private fun rememberBounceFractions(): List<State<Float>> {
    // List size is constant, so calling a composable animation in a loop is safe here.
    val transition = rememberInfiniteTransition(label = "bounce")
    return List(BAR_COUNT) { index -> transition.bounceFraction(index) }
}

@Composable
private fun InfiniteTransition.bounceFraction(barIndex: Int): State<Float> =
    animateFloat(
        initialValue = MIN_BAR_FRACTION,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BASE_BOUNCE_MILLIS + (barIndex % BOUNCE_MILLIS_VARIANTS) * BOUNCE_MILLIS_STEP,
                easing = FastOutSlowInEasing,
            ),
            // Reverse makes the bar go up and then down, forever.
            repeatMode = RepeatMode.Reverse,
            // Fast-forwarding makes each bar start at a different point, so none begins flat.
            initialStartOffset = StartOffset(barIndex * START_OFFSET_STEP_MILLIS, StartOffsetType.FastForward),
        ),
        label = "bar$barIndex",
    )

@Composable
private fun rememberBarBrush(): Brush {
    val colors = MaterialTheme.colorScheme
    // Lime on top, magenta at the bottom. Rebuilt only when the theme colours change.
    return remember(colors.primary, colors.secondary) {
        Brush.verticalGradient(listOf(colors.primary, colors.secondary))
    }
}

private fun DrawScope.drawBars(
    bounceFractions: List<State<Float>>,
    collapseFraction: Float,
    brush: Brush,
) {
    // The bar width that makes all bars plus their gaps fill the width exactly.
    val barWidth = size.width / (BAR_COUNT + (BAR_COUNT - 1) * BAR_GAP_TO_WIDTH_RATIO)
    // Distance from the left edge of one bar to the left edge of the next.
    val barStep = barWidth * (1 + BAR_GAP_TO_WIDTH_RATIO)
    // A collapsed bar is as tall as it is wide, which makes it a dot.
    val dotHeight = barWidth

    bounceFractions.forEachIndexed { index, bounceFraction ->
        // Blend from the bouncing height to a dot as collapseFraction goes from 0 to 1.
        val barHeight = lerp(bounceFraction.value * size.height, dotHeight, collapseFraction)
        drawRoundRect(
            brush = brush,
            // y centres the bar vertically, so bars grow up and down together.
            topLeft = Offset(x = index * barStep, y = (size.height - barHeight) / 2),
            size = Size(barWidth, barHeight),
            // Half the width makes the ends fully round.
            cornerRadius = CornerRadius(barWidth / 2),
        )
    }
}
