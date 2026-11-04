package com.moisesai.challange.feature.songdetails.presentation.composable.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeekBarComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val reportedProgress = mutableListOf<Float>()

    @Test
    fun GIVEN_a_seek_bar_WHEN_the_user_taps_its_middle_THEN_about_half_is_reported() {
        show()

        composeTestRule.onRoot().performTouchInput { click(Offset(width * HALF, centerY)) }

        composeTestRule.waitUntil { reportedProgress.isNotEmpty() }
        assertEquals(HALF, reportedProgress.last(), TOLERANCE)
    }

    @Test
    fun GIVEN_a_seek_bar_WHEN_the_user_drags_to_the_end_THEN_a_progress_near_the_end_is_reported() {
        show()

        composeTestRule.onRoot().performTouchInput { swipeRight() }

        composeTestRule.waitUntil { reportedProgress.isNotEmpty() }
        assertTrue(reportedProgress.last() > NEAR_END)
    }

    private fun show() {
        composeTestRule.setContent {
            MoisesaiChallangeTheme { SeekBarComponent(progress = 0f, onProgressChangedByUser = { reportedProgress += it }) }
        }
    }

    private companion object {
        const val HALF = 0.5f
        const val TOLERANCE = 0.1f
        const val NEAR_END = 0.8f
    }
}
