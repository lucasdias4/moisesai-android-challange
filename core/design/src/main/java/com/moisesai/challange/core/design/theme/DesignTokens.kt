package com.moisesai.challange.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/** The colours and text styles of the design that go beyond Material's own roles. */
@Immutable
class DesignTokens(
    val colors: DesignColors,
    val text: DesignTextStyles,
)

internal val DesignTokensDefault = DesignTokens(colors = DesignColorsDefault, text = DesignTextStylesDefault)

internal val LocalDesign = staticCompositionLocalOf<DesignTokens> {
    error("MoisesaiChallangeTheme is missing: the design tokens are only available inside it.")
}

/** The design tokens: `MaterialTheme.design.colors.text03`, `MaterialTheme.design.text.text16`. */
val MaterialTheme.design: DesignTokens
    @Composable get() = LocalDesign.current
