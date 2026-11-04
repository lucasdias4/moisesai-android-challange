package com.moisesai.challange.core.design.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SheetAnimationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var isVisible by mutableStateOf(false)
    private var dismissCount = 0

    @Test
    fun GIVEN_a_hidden_sheet_WHEN_the_screen_is_shown_THEN_the_sheet_content_does_not_exist() {
        show()

        composeTestRule.onNodeWithText(SHEET_TEXT).assertDoesNotExist()
    }

    @Test
    fun GIVEN_a_hidden_sheet_WHEN_it_becomes_visible_THEN_the_sheet_content_is_shown() {
        show()

        composeTestRule.runOnIdle { isVisible = true }

        composeTestRule.onNodeWithText(SHEET_TEXT).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_visible_sheet_WHEN_it_becomes_hidden_THEN_the_sheet_content_is_gone_after_the_exit() {
        show()
        composeTestRule.runOnIdle { isVisible = true }

        composeTestRule.runOnIdle { isVisible = false }

        composeTestRule.onNodeWithText(SHEET_TEXT).assertDoesNotExist()
    }

    @Test
    fun GIVEN_a_visible_sheet_WHEN_the_user_taps_the_scrim_THEN_the_dismiss_is_asked_for_once() {
        show()
        composeTestRule.runOnIdle { isVisible = true }

        composeTestRule.onNodeWithText(SCREEN_TEXT).performTouchInput { click(topCenter.copy(y = TOP_TAP_Y)) }

        assertEquals(1, dismissCount)
    }

    @Test
    fun GIVEN_a_visible_sheet_WHEN_the_user_pulls_it_down_THEN_the_dismiss_is_asked_for_once() {
        show()
        composeTestRule.runOnIdle { isVisible = true }

        composeTestRule.onNodeWithTag(SHEET_TAG).performTouchInput { swipeDown() }

        composeTestRule.waitUntil { dismissCount == 1 }
        assertEquals(1, dismissCount)
    }

    private fun show() {
        composeTestRule.setContent {
            MoisesaiChallangeTheme {
                Box(Modifier.fillMaxSize()) {
                    Text(SCREEN_TEXT)
                    SheetAnimation(isVisible = isVisible, onDismiss = { dismissCount++ }) {
                        Box(Modifier.size(SHEET_SIZE.dp).testTag(SHEET_TAG)) { Text(SHEET_TEXT) }
                    }
                }
            }
        }
    }

    private companion object {
        const val SCREEN_TEXT = "screen behind"
        const val SHEET_TEXT = "sheet content"
        const val SHEET_TAG = "sheet"
        const val SHEET_SIZE = 200
        const val TOP_TAP_Y = 5f
    }
}
