package com.moisesai.challange.splash

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.splash.animation.SplashPhase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private companion object {
        const val REVEAL_MARGIN_MILLIS = 100L
    }

    private val title = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.splash_title)
    private val totalDurationMillis = SplashPhase.entries.sumOf { it.durationMillis }
    private val untilExitMillis = totalDurationMillis - SplashPhase.Exiting.durationMillis

    @Test
    fun GIVEN_the_splash_is_shown_WHEN_only_half_of_the_time_has_passed_THEN_it_does_not_finish() {
        var finishedCount = 0
        showSplash(onFinished = { finishedCount++ })

        composeTestRule.mainClock.advanceTimeBy(totalDurationMillis / 2)

        assertEquals(0, finishedCount)
    }

    @Test
    fun GIVEN_the_splash_is_shown_WHEN_all_steps_have_passed_THEN_it_finishes_once() {
        var finishedCount = 0
        showSplash(onFinished = { finishedCount++ })

        composeTestRule.mainClock.advanceTimeBy(totalDurationMillis + 1_000)

        assertEquals(1, finishedCount)
    }

    @Test
    fun GIVEN_the_splash_is_shown_WHEN_the_title_is_revealed_THEN_the_app_name_is_displayed() {
        showSplash(onFinished = {})

        composeTestRule.mainClock.advanceTimeBy(untilExitMillis - REVEAL_MARGIN_MILLIS)

        composeTestRule.onNodeWithContentDescription(title).assertIsDisplayed()
    }

    private fun showSplash(onFinished: () -> Unit) {
        // The bars animate forever, so the test clock is advanced by hand.
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            MoisesaiChallangeTheme { SplashScreen(onFinished = onFinished) }
        }
    }
}
