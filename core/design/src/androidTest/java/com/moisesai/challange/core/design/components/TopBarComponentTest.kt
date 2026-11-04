package com.moisesai.challange.core.design.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.strings.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopBarComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun GIVEN_a_title_WHEN_the_bar_is_shown_THEN_the_title_and_the_back_button_are_there() {
        composeTestRule.setContent { MoisesaiChallangeTheme { TopBarComponent(title = "Discovery", onBackClick = {}) } }

        composeTestRule.onNodeWithText("Discovery").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.common_back)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_bar_WHEN_the_user_taps_back_THEN_the_tap_is_reported_once() {
        var backCount = 0
        composeTestRule.setContent { MoisesaiChallangeTheme { TopBarComponent(title = "Discovery", onBackClick = { backCount++ }) } }

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.common_back)).performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun GIVEN_an_action_at_the_end_WHEN_the_bar_is_shown_THEN_the_action_is_there() {
        composeTestRule.setContent {
            MoisesaiChallangeTheme { TopBarComponent(title = "Discovery", onBackClick = {}, endAction = { Text("end action") }) }
        }

        composeTestRule.onNodeWithText("end action").assertIsDisplayed()
    }
}
