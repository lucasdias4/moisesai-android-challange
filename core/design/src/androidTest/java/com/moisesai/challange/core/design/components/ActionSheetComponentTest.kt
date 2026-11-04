package com.moisesai.challange.core.design.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.R as DesignR
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActionSheetComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var itemClickCount = 0

    @Test
    fun GIVEN_a_sheet_about_a_song_WHEN_it_is_shown_THEN_the_title_the_subtitle_and_the_choice_are_there() {
        show()

        composeTestRule.onNodeWithText("Get Lucky").assertIsDisplayed()
        composeTestRule.onNodeWithText("Daft Punk").assertIsDisplayed()
        composeTestRule.onNodeWithText("View album").assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_sheet_with_a_choice_WHEN_the_user_taps_the_choice_THEN_the_tap_is_reported_once() {
        show()

        composeTestRule.onNodeWithText("View album").performClick()

        assertEquals(1, itemClickCount)
    }

    private fun show() {
        composeTestRule.setContent {
            MoisesaiChallangeTheme {
                ActionSheetComponent(title = "Get Lucky", subtitle = "Daft Punk") {
                    ActionSheetItemComponent(iconRes = DesignR.drawable.ic_setlist, text = "View album", onClick = { itemClickCount++ })
                }
            }
        }
    }
}
