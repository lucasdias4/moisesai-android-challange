package com.moisesai.challange.core.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.preview.FullPreview
import com.moisesai.challange.core.design.preview.PreviewSurface
import com.moisesai.challange.core.design.theme.design
import com.moisesai.challange.core.strings.R

// The design leaves this much between the status bar and the bar.
private val SpaceBelowStatusBar = 20.dp
private val BarHeight = 48.dp

// Where the design puts each part inside the bar, measured from its start and its top. The buttons sit 3 above the
// bar and the title 13.5 below it, so that is what is drawn here.
private val BackButtonStart = 7.dp
private val TitleStart = 59.dp
private val EndButtonEnd = 9.dp
private val ButtonTop = (-3).dp
private val TitleTop = 13.5.dp

/**
 * The top of a detail screen: room for the status bar, then a back button, the [title] and, if there is one, a button
 * at the end ([endAction]).
 */
@Composable
fun TopBarComponent(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier, endAction: (@Composable () -> Unit)? = null) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(statusBarHeight + SpaceBelowStatusBar))
        Box(modifier = Modifier.fillMaxWidth().height(BarHeight)) {
            IconButtonComponent(
                iconRes = DesignR.drawable.ic_arrow_left,
                contentDescription = stringResource(R.string.common_back),
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).offset(x = BackButtonStart, y = ButtonTop),
            )
            Text(
                text = title,
                modifier = Modifier.align(Alignment.TopStart).offset(x = TitleStart, y = TitleTop),
                style = MaterialTheme.design.text.display18,
                color = MaterialTheme.design.colors.text03,
                maxLines = 1,
            )
            if (endAction != null) {
                Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = -EndButtonEnd, y = ButtonTop)) { endAction() }
            }
        }
    }
}

@FullPreview
@Composable
private fun TopBarPreview() = PreviewSurface { TopBarComponent(title = "Album Title", onBackClick = {}) }

@FullPreview
@Composable
private fun TopBarWithEndActionPreview() = PreviewSurface {
    TopBarComponent(
        title = "Now playing",
        onBackClick = {},
        endAction = { IconButtonComponent(iconRes = DesignR.drawable.ic_more, contentDescription = "More", onClick = {}) },
    )
}
