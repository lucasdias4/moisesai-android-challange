package com.moisesai.challange.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val AuroraColorScheme = darkColorScheme(
    primary = AuroraLime,
    onPrimary = AuroraNight,
    secondary = AuroraMagenta,
    onSecondary = AuroraNight,
    tertiary = AuroraMagenta,
    onTertiary = AuroraNight,
    background = AuroraNight,
    onBackground = AuroraMist,
    surface = AuroraSmoke,
    onSurface = AuroraMist,
    surfaceVariant = AuroraCharcoal,
    onSurfaceVariant = AuroraAsh,
    outline = AuroraOutline,
)

private val AuroraTokensDefault = AuroraTokens(
    colors = AuroraColors(
        glassFill = Color.White.copy(alpha = 0.065f),
        fieldFill = AuroraSmoke.copy(alpha = 0.45f),
        chipFill = Color.White.copy(alpha = 0.07f),
        chipBorder = Color.White.copy(alpha = 0.14f),
        ghostFill = Color.White.copy(alpha = 0.03f),
        ghostBorder = Color.White.copy(alpha = 0.22f),
        badgeFill = AuroraNight.copy(alpha = 0.45f),
        cardArtist = Color(0xFFD6D6E0),
        labelLine = Color.White.copy(alpha = 0.20f),
    ),
    text = AuroraTextStylesDefault,
)

@Composable
fun MoisesaiChallangeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AuroraColorScheme,
        typography = Typography,
        shapes = AuroraShapes,
    ) {
        CompositionLocalProvider(LocalAurora provides AuroraTokensDefault, content = content)
    }
}
