package com.moisesai.challange.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** The colours of the design, under the names the Figma file gives them (`Dark/Text/text-03` is `text03`). */
@Immutable
class Colors(
    val background00: Color,
    val background03: Color,
    /** A panel that lets what is behind it show: 80% of #262626. */
    val backgroundAlpha01: Color,
    val text00: Color,
    val text01: Color,
    val text02: Color,
    val text03: Color,
    val element00: Color,
    val element03: Color,
    val element04: Color,
    val element05: Color,
    val element07: Color,
    /** White at 10%, 15%, 25%, 50% and 90%: the layers drawn over the black background. */
    val alphaInvert10: Color,
    val alphaInvert15: Color,
    val alphaInvert25: Color,
    val alphaInvert50: Color,
    val alphaInvert90: Color,
    /** `Base/White Alpha`: white at 20%, 60% and 70%. */
    val whiteAlpha20: Color,
    val whiteAlpha60: Color,
    val whiteAlpha70: Color,
    val accentAcqua: Color,
    val accentMint: Color,
)

internal val ColorsDefault = Colors(
    background00 = Color(0xFF000000),
    background03 = Color(0xFF2D2D2D),
    backgroundAlpha01 = Color(0xFF262626).copy(alpha = 0.8f),
    text00 = Color(0xFF737373),
    text01 = Color(0xFFA8A8A8),
    text02 = Color(0xFFD1D1D1),
    text03 = Color(0xFFFFFFFF),
    element00 = Color(0xFF000000),
    element03 = Color(0xFF545454),
    element04 = Color(0xFF858585),
    element05 = Color(0xFFBFBFBF),
    element07 = Color(0xFFFFFFFF),
    alphaInvert10 = Color.White.copy(alpha = 0.10f),
    alphaInvert15 = Color.White.copy(alpha = 0.15f),
    alphaInvert25 = Color.White.copy(alpha = 0.25f),
    alphaInvert50 = Color.White.copy(alpha = 0.50f),
    alphaInvert90 = Color.White.copy(alpha = 0.90f),
    whiteAlpha20 = Color.White.copy(alpha = 0.20f),
    whiteAlpha60 = Color.White.copy(alpha = 0.60f),
    whiteAlpha70 = Color.White.copy(alpha = 0.70f),
    accentAcqua = Color(0xFF00DAE8),
    accentMint = Color(0xFF0AFFA7),
)
