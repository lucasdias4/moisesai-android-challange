package com.moisesai.challange.core.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.moisesai.challange.core.design.R

private val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

// Space Grotesk stops at Bold, so the display styles use Inter Black for a heavier weight.
private val InterBlackFamily = FontFamily(Font(R.font.inter_black, FontWeight.Black))

private val DisplayLetterSpacing = (-0.04).em

// The brand wordmark is set in displayLarge, sized to span most of a phone's width.
private val BrandDisplayFontSize = 64.sp
private val BrandDisplayLineHeight = 72.sp

private fun TextStyle.inSpaceGrotesk() = copy(fontFamily = SpaceGroteskFamily)

private fun TextStyle.asTightDisplay() = copy(
    fontFamily = InterBlackFamily,
    fontWeight = FontWeight.Black,
    letterSpacing = DisplayLetterSpacing,
)

private val MaterialDefaults = Typography()

val Typography = Typography(
    displayLarge = MaterialDefaults.displayLarge.asTightDisplay().copy(
        fontSize = BrandDisplayFontSize,
        lineHeight = BrandDisplayLineHeight,
    ),
    displayMedium = MaterialDefaults.displayMedium.asTightDisplay(),
    displaySmall = MaterialDefaults.displaySmall.asTightDisplay(),
    headlineLarge = MaterialDefaults.headlineLarge.inSpaceGrotesk(),
    headlineMedium = MaterialDefaults.headlineMedium.inSpaceGrotesk(),
    headlineSmall = MaterialDefaults.headlineSmall.inSpaceGrotesk(),
    titleLarge = MaterialDefaults.titleLarge.inSpaceGrotesk(),
    titleMedium = MaterialDefaults.titleMedium.inSpaceGrotesk(),
    titleSmall = MaterialDefaults.titleSmall.inSpaceGrotesk(),
    bodyLarge = MaterialDefaults.bodyLarge.inSpaceGrotesk(),
    bodyMedium = MaterialDefaults.bodyMedium.inSpaceGrotesk(),
    bodySmall = MaterialDefaults.bodySmall.inSpaceGrotesk(),
    labelLarge = MaterialDefaults.labelLarge.inSpaceGrotesk(),
    labelMedium = MaterialDefaults.labelMedium.inSpaceGrotesk(),
    labelSmall = MaterialDefaults.labelSmall.inSpaceGrotesk(),
)
