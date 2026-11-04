package com.moisesai.challange.core.design.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.theme.design

// Taller than the design's 56 dp so the item is easy to hit.
private val ItemHeight = 72.dp

// 24 dp of margin the sheet used to keep around its items, plus the 8 dp of the design.
private val ItemHorizontalPadding = 32.dp
private val SpaceBetweenIconAndText = 16.dp
private val IconSize = 24.dp

/** One choice in an action sheet: an icon and a [text]. The whole row, edge to edge, can be clicked. */
@Composable
fun ActionSheetItemComponent(@DrawableRes iconRes: Int, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val design = MaterialTheme.design
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ItemHeight)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = ItemHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(SpaceBetweenIconAndText),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = null, modifier = Modifier.size(IconSize), tint = design.colors.text03)
        Text(text = text, style = design.text.text16, color = design.colors.text03)
    }
}

@FullPreview
@Composable
private fun ActionSheetItemPreview() = PreviewSurface {
    ActionSheetItemComponent(iconRes = DesignR.drawable.ic_setlist, text = "View album", onClick = {})
}
