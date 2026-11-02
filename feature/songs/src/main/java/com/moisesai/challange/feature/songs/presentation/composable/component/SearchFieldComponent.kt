package com.moisesai.challange.feature.songs.presentation.composable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R

private val FieldHeight = 44.dp
private val FieldHorizontalPadding = 16.dp
private val FieldVerticalPadding = 8.dp
private val SpaceBetweenIconAndText = 8.dp
private val SearchIconSize = 24.dp

/** The box where the user types what to search for. It shows [placeholder] while it is empty. */
@Composable
internal fun SearchFieldComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.design.colors
    val textStyle = MaterialTheme.design.text.text16
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FieldHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(colors.alphaInvert10)
            .padding(horizontal = FieldHorizontalPadding, vertical = FieldVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(SpaceBetweenIconAndText),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(SearchIconSize),
            tint = colors.alphaInvert25,
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(text = stringResource(R.string.songs_search_placeholder), style = textStyle, color = colors.text01)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = textStyle.copy(color = colors.text03),
                singleLine = true,
                cursorBrush = SolidColor(colors.text03),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
            )
        }
    }
}

@FullPreview
@Composable
private fun SearchFieldEmptyPreview() = PreviewSurface {
    SearchFieldComponent(query = "", onQueryChange = {}, onSearchSubmit = {})
}

@FullPreview
@Composable
private fun SearchFieldTypedPreview() = PreviewSurface {
    SearchFieldComponent(query = "Levitating", onQueryChange = {}, onSearchSubmit = {})
}
