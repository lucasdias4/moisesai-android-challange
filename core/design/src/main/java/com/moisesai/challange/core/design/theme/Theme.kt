package com.moisesai.challange.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun MoisesaiChallangeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AuroraColorScheme,
        typography = Typography,
        content = content,
    )
}
