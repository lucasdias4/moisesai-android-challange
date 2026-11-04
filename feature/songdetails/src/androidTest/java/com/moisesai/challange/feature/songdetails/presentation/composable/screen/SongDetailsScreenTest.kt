package com.moisesai.challange.feature.songdetails.presentation.composable.screen

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.design.components.SKELETON_TEST_TAG
import com.moisesai.challange.core.design.model.UiErrorType
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songdetails.presentation.composable.component.preview_helper.SamplePlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiPlayingTrack
import com.moisesai.challange.feature.songdetails.presentation.model.UiSongDetailsUiState
import com.moisesai.challange.feature.songdetails.presentation.support.RecordingSongDetailsCallbacks
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val callbacks = RecordingSongDetailsCallbacks()

    @Test
    fun GIVEN_the_track_is_being_prepared_WHEN_the_screen_is_shown_THEN_a_skeleton_stands_where_it_will_be_and_there_are_no_controls() {
        show(UiSongDetailsUiState.Loading)

        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_play)).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_pause)).assertDoesNotExist()
    }

    @Test
    fun GIVEN_the_track_failed_to_load_WHEN_the_user_taps_try_again_THEN_the_retry_is_asked_for() {
        show(UiSongDetailsUiState.Error(UiErrorType.Network))

        composeTestRule.onNodeWithText(text(R.string.common_error_network_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(text(R.string.common_retry)).performClick()

        assertEquals(1, callbacks.retryCount)
    }

    @Test
    fun GIVEN_the_track_is_playing_WHEN_the_screen_is_shown_THEN_its_names_and_the_pause_button_are_there() {
        show(success(SamplePlayingTrack.copy(isPlaying = true)))

        composeTestRule.onNodeWithText(SamplePlayingTrack.trackName).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_pause)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_play)).assertDoesNotExist()
    }

    @Test
    fun GIVEN_the_track_is_paused_WHEN_the_user_taps_play_THEN_the_tap_is_reported() {
        show(success(SamplePlayingTrack.copy(isPlaying = false)))

        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_play)).performClick()

        assertEquals(1, callbacks.playPauseCount)
    }

    @Test
    fun GIVEN_the_track_cannot_play_WHEN_the_user_taps_play_THEN_the_tap_is_still_reported_so_the_message_can_be_shown() {
        show(success(SamplePlayingTrack.copy(isPlaying = false, canPlay = false)))

        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_play)).performClick()

        assertEquals(1, callbacks.playPauseCount)
    }

    @Test
    fun GIVEN_the_message_that_it_cannot_play_is_asked_for_WHEN_the_screen_is_shown_THEN_the_message_appears() {
        show(success(SamplePlayingTrack.copy(canPlay = false), isCannotPlayMessageVisible = true))

        composeTestRule.waitUntil(timeoutMillis = WAIT_MILLIS) {
            composeTestRule.onAllNodesWithText(text(R.string.songdetails_cannot_play)).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(text(R.string.songdetails_cannot_play)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_track_WHEN_the_user_taps_previous_next_and_repeat_THEN_each_tap_is_reported() {
        show(success(SamplePlayingTrack))

        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_previous)).performClick()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_next)).performClick()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_repeat)).performClick()

        assertEquals(listOf(1, 1, 1), listOf(callbacks.previousCount, callbacks.nextCount, callbacks.repeatCount))
    }

    @Test
    fun GIVEN_a_track_WHEN_a_screen_reader_sets_the_progress_of_the_bar_THEN_the_chosen_point_is_reported() {
        show(success(SamplePlayingTrack))

        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .performSemanticsAction(SemanticsActions.SetProgress) { setProgress -> setProgress(HALF) }

        assertEquals(listOf(HALF), callbacks.seekProgresses)
    }

    @Test
    fun GIVEN_the_sheet_of_the_track_is_open_WHEN_the_user_taps_view_album_THEN_the_album_of_the_track_is_reported() {
        show(success(SamplePlayingTrack, isTrackOptionsVisible = true))

        composeTestRule.onNodeWithText(text(R.string.common_view_album)).performClick()

        assertEquals(listOf(SamplePlayingTrack.albumId), callbacks.albumIds)
    }

    @Test
    fun GIVEN_a_track_WHEN_the_user_taps_back_THEN_the_tap_is_reported() {
        show(success(SamplePlayingTrack))

        composeTestRule.onNodeWithContentDescription(text(R.string.common_back)).performClick()

        assertEquals(1, callbacks.backCount)
    }

    @Test
    fun GIVEN_a_wide_screen_WHEN_the_track_is_shown_THEN_the_names_and_every_control_are_still_visible() {
        show(success(SamplePlayingTrack), modifier = Modifier.size(width = WIDE_WIDTH.dp, height = WIDE_HEIGHT.dp))

        composeTestRule.onNodeWithText(SamplePlayingTrack.trackName).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_play)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(text(R.string.songdetails_next)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_track_is_shown_WHEN_the_next_track_arrives_THEN_its_names_replace_the_old_ones() {
        var shownTrack by mutableStateOf(SamplePlayingTrack)
        composeTestRule.setContent {
            MoisesaiChallangeTheme {
                SongDetailsScreen(uiState = success(shownTrack), callbacks = callbacks.asSongDetailsCallbacks())
            }
        }

        shownTrack = SamplePlayingTrack.copy(id = 2L, trackName = "Instant Crush")

        composeTestRule.waitUntil(timeoutMillis = WAIT_MILLIS) {
            composeTestRule.onAllNodesWithText("Instant Crush").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Instant Crush").assertIsDisplayed()
        composeTestRule.onAllNodesWithText(SamplePlayingTrack.trackName).assertCountEquals(0)
    }

    private fun show(uiState: UiSongDetailsUiState, modifier: Modifier = Modifier) {
        composeTestRule.setContent {
            MoisesaiChallangeTheme {
                SongDetailsScreen(uiState = uiState, callbacks = callbacks.asSongDetailsCallbacks(), modifier = modifier)
            }
        }
    }

    private fun success(playingTrack: UiPlayingTrack, isTrackOptionsVisible: Boolean = false, isCannotPlayMessageVisible: Boolean = false) =
        UiSongDetailsUiState.Success(
            playingTrack = playingTrack,
            isTrackOptionsVisible = isTrackOptionsVisible,
            isCannotPlayMessageVisible = isCannotPlayMessageVisible,
        )

    private fun text(id: Int) = context.getString(id)

    private companion object {
        const val WAIT_MILLIS = 5_000L
        const val HALF = 0.5f

        // A phone on its side, in dp: wider than it is tall, so the screen uses its landscape layout.
        const val WIDE_WIDTH = 915
        const val WIDE_HEIGHT = 411
    }
}
