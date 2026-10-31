package com.moisesai.challange.core.design.preview

import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Phone - Default")
@Preview(name = "Phone - Dark Mode", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Preview(name = "Phone - Small", widthDp = 320, uiMode = AndroidUiModes.UI_MODE_NIGHT_NO)
@Preview(name = "Tablet - Default", device = Devices.PIXEL_TABLET)
@Preview(name = "Tablet - Dark Mode", device = Devices.PIXEL_TABLET, uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
annotation class FullPreview
