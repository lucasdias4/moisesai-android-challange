package com.moisesai.challange.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DesignColorScheme = darkColorScheme(
    primary = ColorsDefault.element07,
    onPrimary = ColorsDefault.element00,
    background = ColorsDefault.background00,
    onBackground = ColorsDefault.text03,
    surface = ColorsDefault.background00,
    onSurface = ColorsDefault.text03,
    surfaceVariant = ColorsDefault.background03,
    onSurfaceVariant = ColorsDefault.text01,
    outline = ColorsDefault.element03,
)

/** The look of the app: the colours, shapes and text styles of the Figma design. It is dark only. */
@Composable
fun MoisesaiChallangeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DesignColorScheme, shapes = Shapes) {
        CompositionLocalProvider(LocalDesign provides TokensDefault, content = content)
    }
}
