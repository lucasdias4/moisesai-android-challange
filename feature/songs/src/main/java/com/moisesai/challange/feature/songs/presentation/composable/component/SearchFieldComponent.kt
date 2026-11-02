package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.components.GlassState
import com.moisesai.challange.core.design.components.GlassStyle
import com.moisesai.challange.core.design.components.glass
import com.moisesai.challange.core.design.components.glassEdge
import com.moisesai.challange.core.design.components.rememberGlassState
import com.moisesai.challange.core.design.graphics.cssBoxShadow
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.theme.aurora
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.animation.rememberCaretBlinkAlpha
import com.moisesai.challange.feature.songs.presentation.composable.animation.searchFieldFocusAnimation
import com.moisesai.challange.feature.songs.presentation.composable.animation.searchFieldHintAnimation
import com.moisesai.challange.feature.songs.presentation.composable.component.icon.FieldSearchGlyph
import com.moisesai.challange.feature.songs.presentation.composable.component.layout.SearchFieldHeight
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.PreviewSurface
import com.moisesai.challange.feature.songs.presentation.composable.model.SongsActions
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import kotlin.math.roundToInt

private val FieldHorizontalPadding = 20.dp
private val IconSize = 20.dp
private val IconToTextGap = 10.dp

// The caret is a thin lime bar with a soft glow. In the design it sits a little to the right of the end of the
// text, so that is where it is drawn when the cursor is at the end; inside the text it sits exactly on the cursor.
private val CaretWidth = 2.dp
private val CaretHeight = 20.dp
private val CaretGlowBlur = 10.dp
private val CaretGapAfterText = 10.dp

// The hint in the empty field is the text colour at about two thirds, so it reads as a hint and not as content.
private const val PLACEHOLDER_ALPHA = 0.62f

// While the field is not used the glass behind it is a little more solid on Android versions without real blur.
private const val FIELD_FALLBACK_ALPHA = 0.7f

/**
 * The glass pill where the user types. It pulses a hint on the empty home, glows when focused, and searches from the
 * keyboard. Its text and caret are drawn here (not by the system) so they look exactly like the design's.
 */
@Composable
internal fun SearchFieldComponent(
    uiState: UiSongsUiState,
    actions: SongsActions,
    glassState: GlassState,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = MaterialTheme.shapes.extraLarge
    val colors = MaterialTheme.colorScheme
    val fieldFill = MaterialTheme.aurora.colors.fieldFill
    val description = stringResource(R.string.songs_search_field_description)
    // The hint pulses only where the invitation is: an empty home whose field has not been touched.
    val isHintActive = uiState.content is UiSongsContentState.FirstLaunch &&
        uiState.query.isEmpty() && !isFocused

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchFieldHeight)
            .searchFieldHintAnimation(isHintActive, shape)
            .searchFieldFocusAnimation(isFocused, shape)
            .glass(glassState, GlassStyle(shape, fieldFill, fallbackFill = fieldFill.copy(alpha = FIELD_FALLBACK_ALPHA)))
            .glassEdge(shape)
            .padding(horizontal = FieldHorizontalPadding)
            .testTag("songs-search-field"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(IconToTextGap),
    ) {
        Icon(
            imageVector = FieldSearchGlyph,
            contentDescription = null,
            tint = if (isFocused) colors.primary else colors.onSurfaceVariant,
            modifier = Modifier.size(IconSize),
        )
        QueryTextComponent(
            query = uiState.query,
            isFocused = isFocused,
            description = description,
            onQueryChange = actions.onQueryChange,
            onSearchSubmit = actions.onSearchSubmit,
            interactionSource = interactionSource,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QueryTextComponent(
    query: String,
    isFocused: Boolean,
    description: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val text = MaterialTheme.aurora.text
    val colors = MaterialTheme.colorScheme
    // The field keeps its own cursor and selection; the screen only knows the text.
    var field by remember { mutableStateOf(TextFieldValue(query)) }
    LaunchedEffect(query) {
        if (field.text != query) field = TextFieldValue(query, TextRange(query.length))
    }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(modifier = modifier) {
        // The hint goes away as soon as the field is focused, even while it is empty, so the caret has the place to itself.
        if (query.isEmpty() && !isFocused) {
            Text(
                text = stringResource(R.string.songs_search_placeholder),
                style = text.placeholder,
                color = colors.onBackground.copy(alpha = PLACEHOLDER_ALPHA),
                maxLines = 1,
            )
        }
        BasicTextField(
            value = field,
            onValueChange = { changed ->
                field = changed
                if (changed.text != query) onQueryChange(changed.text)
            },
            singleLine = true,
            textStyle = text.field.copy(color = colors.onBackground),
            // The system draws no caret: the design's own is drawn below.
            cursorBrush = SolidColor(Color.Transparent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
            interactionSource = interactionSource,
            onTextLayout = { layout = it },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = description },
        )
        if (isFocused && field.selection.collapsed) {
            CaretComponent(layout = { layout }, cursorIndex = field.selection.start, textLength = field.text.length)
        }
    }
}

@Composable
private fun CaretComponent(layout: () -> TextLayoutResult?, cursorIndex: Int, textLength: Int) {
    val blink = rememberCaretBlinkAlpha()
    val lime = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .offset {
                val cursor = layout()?.getCursorRect(cursorIndex)
                val gap = if (cursorIndex == textLength) CaretGapAfterText.roundToPx() else 0
                val x = (cursor?.left ?: 0f).roundToInt() + gap
                // Centred on the line of text, which is a little taller than the caret.
                val lineHeight = layout()?.getLineBottom(0)?.roundToInt() ?: 0
                IntOffset(x, (lineHeight - CaretHeight.roundToPx()) / 2)
            }
            .size(CaretWidth, CaretHeight)
            .graphicsLayer { alpha = blink.value }
            .cssBoxShadow(RectangleShape, lime, blur = CaretGlowBlur)
            .background(lime),
    )
}

@Composable
private fun SearchFieldPreviewOf(uiState: UiSongsUiState) = PreviewSurface {
    SearchFieldComponent(uiState = uiState, actions = SongsActions.None, glassState = rememberGlassState())
}

private val FirstLaunchSample = UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable)

@FullPreview
@Composable
private fun SearchFieldEmptyPreview() = SearchFieldPreviewOf(UiSongsUiState(content = FirstLaunchSample))

@FullPreview
@Composable
private fun SearchFieldTypedPreview() =
    SearchFieldPreviewOf(UiSongsUiState(query = "Levitating", content = FirstLaunchSample))
