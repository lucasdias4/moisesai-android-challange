package com.moisesai.challange.splash

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        // The splash lasts as long as all its phases together.
        val SPLASH_DURATION_MILLIS = SplashPhase.entries.sumOf { it.durationMillis }
        const val BEFORE_THE_END_MILLIS = 100L
        const val AFTER_THE_END_MILLIS = 1_000L
    }

    @Test
    fun GIVEN_the_splash_is_shown_WHEN_it_has_not_lasted_long_enough_THEN_it_does_not_finish() {
        var finishedCount = 0
        showSplash(onFinished = { finishedCount++ })

        composeTestRule.mainClock.advanceTimeBy(SPLASH_DURATION_MILLIS - BEFORE_THE_END_MILLIS)

        assertEquals(0, finishedCount)
    }

    @Test
    fun GIVEN_the_splash_is_shown_WHEN_its_time_has_passed_THEN_it_finishes_once() {
        var finishedCount = 0
        showSplash(onFinished = { finishedCount++ })

        composeTestRule.mainClock.advanceTimeBy(SPLASH_DURATION_MILLIS + AFTER_THE_END_MILLIS)

        assertEquals(1, finishedCount)
    }

    private fun showSplash(onFinished: () -> Unit) {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            MoisesaiChallangeTheme { SplashScreen(onFinished = onFinished) }
        }
    }
}
