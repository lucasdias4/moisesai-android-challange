package com.moisesai.challange.core.design.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.strings.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorMessageComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var retryCount = 0

    @Test
    fun GIVEN_a_network_error_WHEN_it_is_shown_THEN_it_says_the_user_seems_to_be_offline() {
        show(UiErrorType.Network)

        composeTestRule.onNodeWithText(context.getString(R.string.common_error_network_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.common_error_network_message)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_an_unknown_error_WHEN_it_is_shown_THEN_it_says_something_went_wrong() {
        show(UiErrorType.Unknown)

        composeTestRule.onNodeWithText(context.getString(R.string.common_error_unknown_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.common_error_unknown_message)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_an_error_WHEN_the_user_taps_try_again_THEN_the_retry_is_asked_for_once() {
        show(UiErrorType.Network)

        composeTestRule.onNodeWithText(context.getString(R.string.common_retry)).performClick()

        assertEquals(1, retryCount)
    }

    private fun show(type: UiErrorType) {
        composeTestRule.setContent { MoisesaiChallangeTheme { ErrorMessageComponent(type = type, onRetry = { retryCount++ }) } }
    }
}
