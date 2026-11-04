package com.moisesai.challange.core.design.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoverImageComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun GIVEN_a_cover_without_an_address_WHEN_it_is_shown_THEN_its_empty_content_is_shown() {
        composeTestRule.setContent {
            MoisesaiChallangeTheme {
                CoverImageComponent(artUrl = null, modifier = Modifier.size(COVER_SIZE.dp), emptyContent = { Text("no picture") })
            }
        }

        composeTestRule.onNodeWithText("no picture").assertIsDisplayed()
    }

    private companion object {
        const val COVER_SIZE = 100
    }
}
