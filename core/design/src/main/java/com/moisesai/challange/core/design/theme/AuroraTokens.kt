package com.moisesai.challange.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** Colours the design uses that have no Material role. Each is a fixed translucent value over the dark background. */
@Immutable
class AuroraColors(
    /** A glass surface: white so faint that whatever is behind it still shows. */
    val glassFill: Color,
    /** The search field: a little darker than plain glass so the text stays readable over any tint. */
    val fieldFill: Color,
    val chipFill: Color,
    val chipBorder: Color,
    /** The placeholder card that says where the history will appear. */
    val ghostFill: Color,
    val ghostBorder: Color,
    /** The label on a card, dark enough to read on any artwork. */
    val badgeFill: Color,
    /** The artist line on a card: a cool light grey that sits well on the scrim. */
    val cardArtist: Color,
    /** The hairline that follows a section label. */
    val labelLine: Color,
)

/** Text styles of the design. Material's own `Typography` is closed, so these live next to it. */
@Immutable
class AuroraTextStyles(
    /** The big `moisesai.` title. */
    val title: TextStyle,
    /** The same title once the screen has scrolled. */
    val compactTitle: TextStyle,
    /** The song name on a large card. */
    val cardTitle: TextStyle,
    /** The title of the empty history card. */
    val ghostTitle: TextStyle,
    /** A small uppercase section name; the text is uppercased where it is drawn. */
    val sectionLabel: TextStyle,
    /** The label on a card; stored uppercase in the strings. */
    val badge: TextStyle,
    val rowTitle: TextStyle,
    val chip: TextStyle,
    val subtitle: TextStyle,
    val placeholder: TextStyle,
    /** What the user types in the search field. */
    val field: TextStyle,
    /** Artist and duration lines. */
    val caption: TextStyle,
)

@Immutable
class AuroraTokens(
    val colors: AuroraColors,
    val text: AuroraTextStyles,
)

internal val LocalAurora = staticCompositionLocalOf<AuroraTokens> {
    error("MoisesaiChallangeTheme is missing: Aurora tokens are only available inside it.")
}

/** The design tokens that go beyond Material: `MaterialTheme.aurora.colors` and `MaterialTheme.aurora.text`. */
val MaterialTheme.aurora: AuroraTokens
    @Composable get() = LocalAurora.current
