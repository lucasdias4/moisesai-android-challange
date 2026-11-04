package com.moisesai.challange.core.design.animation

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresenceAnimationsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var isVisible by mutableStateOf(true)
    private var state by mutableStateOf("first")

    @Test
    fun GIVEN_a_visible_collapse_WHEN_it_becomes_hidden_THEN_the_content_is_gone() {
        composeTestRule.setContent { MoisesaiChallangeTheme { CollapseAnimation(isVisible = isVisible) { Text(CONTENT) } } }
        composeTestRule.onNodeWithText(CONTENT).assertIsDisplayed()

        composeTestRule.runOnIdle { isVisible = false }

        composeTestRule.onNodeWithText(CONTENT).assertDoesNotExist()
    }

    @Test
    fun GIVEN_a_hidden_pop_WHEN_it_becomes_visible_THEN_the_content_is_shown() {
        isVisible = false
        composeTestRule.setContent { MoisesaiChallangeTheme { PopAnimation(isVisible = isVisible) { Text(CONTENT) } } }
        composeTestRule.onNodeWithText(CONTENT).assertDoesNotExist()

        composeTestRule.runOnIdle { isVisible = true }

        composeTestRule.onNodeWithText(CONTENT).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_state_animation_WHEN_the_state_changes_THEN_only_the_new_state_is_shown() {
        composeTestRule.setContent { MoisesaiChallangeTheme { StateAnimation(state = state) { Text(it) } } }
        composeTestRule.onNodeWithText("first").assertIsDisplayed()

        composeTestRule.runOnIdle { state = "second" }

        composeTestRule.onNodeWithText("second").assertIsDisplayed()
        composeTestRule.onNodeWithText("first").assertDoesNotExist()
    }

    private companion object {
        const val CONTENT = "content"
    }
}
