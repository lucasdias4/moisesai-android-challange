package com.moisesai.challange.core.design.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.animation.pressAnimation
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design

private val ButtonSize = 48.dp
private val IconSize = 24.dp

/** A round button of 48 with an icon of 24 in the middle, the way the design draws the buttons of a top bar. */
@Composable
fun IconButtonComponent(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ButtonSize)
            .pressAnimation(role = Role.Button, onClick = onClick)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = contentDescription, modifier = Modifier.size(IconSize), tint = MaterialTheme.design.colors.text03)
    }
}

@FullPreview
@Composable
private fun IconButtonPreview() = PreviewSurface {
    IconButtonComponent(iconRes = DesignR.drawable.ic_arrow_left, contentDescription = "Back", onClick = {})
}
