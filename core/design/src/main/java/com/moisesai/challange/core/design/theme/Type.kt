package com.moisesai.challange.core.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.moisesai.challange.core.design.R

// The agreed design only ever loaded Space Grotesk at Medium and Bold, so every "regular" text in it is drawn at
// Medium. Normal therefore maps to the Medium file too: that is what keeps text the same weight as in the design.
internal val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

// Space Grotesk stops at Bold, so the display styles use Inter Black for a heavier weight.
internal val InterBlackFamily = FontFamily(Font(R.font.inter_black, FontWeight.Black))

private val DisplayLetterSpacing = (-0.04).em

// The brand title is set in displayLarge, sized to span most of a phone's width.
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

// Every line height is explicit and centred, because the design was measured with its text centred in a fixed line box.
private val CenteredLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun auroraStyle(
    family: FontFamily,
    weight: FontWeight,
    fontSize: Float,
    lineHeight: Float,
    letterSpacingEm: Float = 0f,
) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacingEm.em,
    lineHeightStyle = CenteredLineHeight,
)

// Sizes, line heights and tracking below are the ones measured in the agreed design (360 wide, 1 dp = 1 px).
internal val AuroraTextStylesDefault = AuroraTextStyles(
    title = auroraStyle(InterBlackFamily, FontWeight.Black, 58f, 55.1f, -0.055f),
    compactTitle = auroraStyle(InterBlackFamily, FontWeight.Black, 22f, 26f, -0.04f),
    cardTitle = auroraStyle(InterBlackFamily, FontWeight.Black, 26f, 26f, -0.045f),
    ghostTitle = auroraStyle(InterBlackFamily, FontWeight.Black, 20f, 24f, -0.04f),
    sectionLabel = auroraStyle(SpaceGroteskFamily, FontWeight.Medium, 11f, 14f, 0.2f),
    badge = auroraStyle(SpaceGroteskFamily, FontWeight.Bold, 11f, 14f, 0.18f),
    rowTitle = auroraStyle(SpaceGroteskFamily, FontWeight.Bold, 16f, 21f),
    chip = auroraStyle(SpaceGroteskFamily, FontWeight.Bold, 16f, 20f),
    subtitle = auroraStyle(SpaceGroteskFamily, FontWeight.Medium, 15f, 19f),
    placeholder = auroraStyle(SpaceGroteskFamily, FontWeight.Medium, 15f, 19f, -0.01f),
    field = auroraStyle(SpaceGroteskFamily, FontWeight.Medium, 16f, 21f),
    caption = auroraStyle(SpaceGroteskFamily, FontWeight.Medium, 13f, 17f),
)
