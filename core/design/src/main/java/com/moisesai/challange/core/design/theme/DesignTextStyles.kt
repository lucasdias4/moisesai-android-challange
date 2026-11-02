package com.moisesai.challange.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

// The design is set in Articulat CF, a commercial font that is not in the project yet. Until its files are added to
// `res/font`, the system font stands in, so the sizes, weights and line heights are right but the letter shapes are not.
internal val DesignFontFamily: FontFamily = FontFamily.Default

private const val DISPLAY_WEIGHT = 600
private const val TEXT_WEIGHT = 500

// The design centres every line of text in its line box; this is what makes Compose do the same.
private val CenteredLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun designStyle(weight: Int, fontSize: Float, lineHeightPercent: Float) = TextStyle(
    fontFamily = DesignFontFamily,
    fontWeight = FontWeight(weight),
    fontSize = fontSize.sp,
    lineHeight = (fontSize * lineHeightPercent / 100f).sp,
    lineHeightStyle = CenteredLineHeight,
)

/** The text styles of the design, under its names: `Display/Display 24` is `display24`, `Text/Text 16` is `text16`. */
@Immutable
class DesignTextStyles(
    val display12: TextStyle,
    val display14: TextStyle,
    val display16: TextStyle,
    val display18: TextStyle,
    val display20: TextStyle,
    val display24: TextStyle,
    val display32: TextStyle,
    val text12: TextStyle,
    val text14: TextStyle,
    val text16: TextStyle,
    val text28: TextStyle,
)

internal val DesignTextStylesDefault = DesignTextStyles(
    display12 = designStyle(DISPLAY_WEIGHT, 12f, 140f),
    display14 = designStyle(DISPLAY_WEIGHT, 14f, 120f),
    display16 = designStyle(DISPLAY_WEIGHT, 16f, 120f),
    display18 = designStyle(DISPLAY_WEIGHT, 18f, 108f),
    display20 = designStyle(DISPLAY_WEIGHT, 20f, 120f),
    display24 = designStyle(DISPLAY_WEIGHT, 24f, 120f),
    display32 = designStyle(DISPLAY_WEIGHT, 32f, 120f),
    text12 = designStyle(TEXT_WEIGHT, 12f, 140f),
    text14 = designStyle(TEXT_WEIGHT, 14f, 120f),
    text16 = designStyle(TEXT_WEIGHT, 16f, 120f),
    text28 = designStyle(TEXT_WEIGHT, 28f, 120f),
)
