package com.moisesai.challange.feature.album.presentation.composable.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.design.components.SKELETON_TEST_TAG
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.album.presentation.composable.callback.AlbumCallbacks
import com.moisesai.challange.feature.album.presentation.composable.component.preview_helper.SampleAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var backCount = 0
    private var retryCount = 0
    private val clickedTrackIds = mutableListOf<Long>()
    private val callbacks = AlbumCallbacks(
        onBackClick = { backCount++ },
        onTrackClick = { clickedTrackIds += it },
        onRetry = { retryCount++ },
    )

    @Test
    fun GIVEN_the_album_is_loading_WHEN_the_screen_is_shown_THEN_a_skeleton_stands_where_the_album_will_be_and_no_track_is_listed() {
        show(UiAlbumUiState.Loading)

        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText(SampleAlbum.tracks.first().name).assertDoesNotExist()
    }

    @Test
    fun GIVEN_the_album_failed_to_load_WHEN_the_user_taps_try_again_THEN_the_retry_is_asked_for() {
        show(UiAlbumUiState.Error(UiErrorType.Network))

        composeTestRule.onNodeWithText(context.getString(R.string.common_error_network_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.common_retry)).performClick()

        assertEquals(1, retryCount)
    }

    @Test
    fun GIVEN_an_album_with_tracks_WHEN_it_is_shown_THEN_its_names_and_its_tracks_are_listed_and_there_is_no_skeleton() {
        show(UiAlbumUiState.Success(SampleAlbum))

        composeTestRule.onAllNodesWithText(SampleAlbum.name).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText(SampleAlbum.tracks.first().name).assertIsDisplayed()
        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).assertCountEquals(0)
    }

    @Test
    fun GIVEN_an_album_with_tracks_WHEN_the_user_taps_a_track_THEN_its_id_is_reported() {
        show(UiAlbumUiState.Success(SampleAlbum))

        composeTestRule.onNodeWithText(SampleAlbum.tracks[1].name).performClick()

        assertEquals(listOf(SampleAlbum.tracks[1].id), clickedTrackIds)
    }

    @Test
    fun GIVEN_an_album_without_tracks_WHEN_it_is_shown_THEN_the_album_is_shown_and_no_track_is_listed() {
        show(UiAlbumUiState.Success(SampleAlbum.copy(tracks = emptyList())))

        composeTestRule.onAllNodesWithText(SampleAlbum.name).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText(SampleAlbum.tracks.first().name).assertDoesNotExist()
    }

    @Test
    fun GIVEN_an_album_WHEN_the_user_taps_back_THEN_the_tap_is_reported() {
        show(UiAlbumUiState.Success(SampleAlbum))

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.common_back)).performClick()

        assertEquals(1, backCount)
    }

    private fun show(uiState: UiAlbumUiState) {
        composeTestRule.setContent { MoisesaiChallangeTheme { AlbumScreen(uiState = uiState, callbacks = callbacks) } }
    }
}
