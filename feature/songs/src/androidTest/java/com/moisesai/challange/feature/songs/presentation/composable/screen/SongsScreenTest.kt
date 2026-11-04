package com.moisesai.challange.feature.songs.presentation.composable.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.design.components.SKELETON_TEST_TAG
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SampleTracks
import com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper.SuggestedSample
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import com.moisesai.challange.feature.songs.presentation.model.UiTrackOptionsState
import com.moisesai.challange.feature.songs.presentation.support.RecordingSongsCallbacks
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val callbacks = RecordingSongsCallbacks()
    private val levitating = SampleTracks.first()

    @Test
    fun GIVEN_the_suggestions_are_loading_WHEN_the_first_launch_is_shown_THEN_a_skeleton_stands_where_the_songs_will_be() {
        showContent(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading))

        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(describe(SuggestedSample.first())).assertDoesNotExist()
    }

    @Test
    fun GIVEN_suggested_songs_WHEN_the_first_launch_is_shown_THEN_each_song_is_listed() {
        showContent(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(SuggestedSample)))

        SuggestedSample.forEach { track -> composeTestRule.onNodeWithContentDescription(describe(track)).assertIsDisplayed() }
        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).assertCountEquals(0)
    }

    @Test
    fun GIVEN_recent_songs_WHEN_the_history_is_shown_THEN_each_song_is_listed() {
        showContent(UiSongsContentState.History(SampleTracks))

        SampleTracks.take(VISIBLE_ROWS).forEach { track -> composeTestRule.onNodeWithContentDescription(describe(track)).assertIsDisplayed() }
    }

    @Test
    fun GIVEN_a_search_is_running_WHEN_nothing_is_known_yet_THEN_a_skeleton_stands_where_the_songs_will_be() {
        showContent(UiSongsContentState.SearchLoading, query = "levitating")

        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).onFirst().assertIsDisplayed()
    }

    @Test
    fun GIVEN_search_results_WHEN_they_are_shown_THEN_each_song_is_listed_and_there_is_no_skeleton() {
        showContent(UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.Complete), query = "a")

        SampleTracks.take(VISIBLE_ROWS).forEach { track -> composeTestRule.onNodeWithContentDescription(describe(track)).assertIsDisplayed() }
        composeTestRule.onAllNodesWithTag(SKELETON_TEST_TAG).assertCountEquals(0)
    }

    @Test
    fun GIVEN_a_search_found_nothing_WHEN_it_is_shown_THEN_the_screen_names_what_was_searched() {
        showContent(UiSongsContentState.SearchNoResults(searchText = "zzzz"), query = "zzzz")

        composeTestRule.onNodeWithText(context.getString(R.string.songs_no_results_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.songs_no_results_message, "zzzz")).assertIsDisplayed()
    }

    @Test
    fun GIVEN_a_search_failed_because_of_the_network_WHEN_the_user_taps_try_again_THEN_the_retry_is_asked_for() {
        showContent(UiSongsContentState.SearchError(UiSongsError.Network), query = "levitating")

        composeTestRule.onNodeWithText(context.getString(R.string.songs_error_network_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.songs_retry)).performClick()

        assertEquals(1, callbacks.retryCount)
    }

    @Test
    fun GIVEN_an_update_failed_over_results_WHEN_they_are_shown_THEN_the_banner_and_the_songs_are_both_there() {
        showContent(UiSongsContentState.SearchResults(SampleTracks, UiSearchResultsState.Failed(UiSongsError.Network)), query = "a")

        composeTestRule.onNodeWithText(context.getString(R.string.songs_partial_error)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(describe(levitating)).assertIsDisplayed()
    }

    @Test
    fun GIVEN_the_search_field_is_tucked_away_WHEN_the_screen_is_shown_THEN_the_magnifier_is_there_and_the_field_is_not() {
        showContent(UiSongsContentState.History(SampleTracks), isSearchFieldVisible = false)

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.songs_search_button)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.songs_search_placeholder)).assertDoesNotExist()
    }

    @Test
    fun GIVEN_the_search_field_is_tucked_away_WHEN_the_user_taps_the_magnifier_THEN_it_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks), isSearchFieldVisible = false)

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.songs_search_button)).performClick()

        assertEquals(1, callbacks.searchIconClickCount)
    }

    @Test
    fun GIVEN_the_search_field_is_shown_WHEN_the_user_types_THEN_the_text_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks))

        composeTestRule.onNode(hasSetTextAction()).performTextInput("lev")

        assertEquals(listOf("lev"), callbacks.typedQueries)
    }

    @Test
    fun GIVEN_the_search_field_is_shown_WHEN_the_user_presses_search_on_the_keyboard_THEN_the_submit_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks))

        composeTestRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals(1, callbacks.submitCount)
    }

    @Test
    fun GIVEN_a_list_of_songs_WHEN_the_user_taps_a_song_THEN_its_id_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks))

        composeTestRule.onNodeWithContentDescription(describe(levitating)).performClick()

        assertEquals(listOf(levitating.id), callbacks.clickedTrackIds)
    }

    @Test
    fun GIVEN_a_list_of_songs_WHEN_the_user_taps_more_on_the_first_song_THEN_that_song_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks))

        composeTestRule.onAllNodesWithContentDescription(context.getString(R.string.common_more_options)).onFirst().performClick()

        assertEquals(listOf(levitating), callbacks.optionsTracks)
    }

    @Test
    fun GIVEN_the_sheet_of_a_song_is_open_WHEN_the_user_taps_view_album_THEN_the_album_of_the_song_is_reported() {
        showContent(UiSongsContentState.History(SampleTracks), trackOptions = UiTrackOptionsState.Visible(levitating))

        composeTestRule.onNodeWithText(context.getString(R.string.songs_view_album)).performClick()

        assertEquals(listOf(levitating.albumId), callbacks.albumIds)
    }

    private fun showContent(
        content: UiSongsContentState,
        query: String = "",
        isSearchFieldVisible: Boolean = true,
        trackOptions: UiTrackOptionsState = UiTrackOptionsState.Hidden,
    ) {
        val uiState = UiSongsUiState(query = query, isSearchFieldVisible = isSearchFieldVisible, content = content, trackOptions = trackOptions)
        composeTestRule.setContent {
            MoisesaiChallangeTheme { SongsScreen(uiState = uiState, callbacks = callbacks.asSongsCallbacks()) }
        }
    }

    private fun describe(track: UiTrack) = context.getString(R.string.songs_track_description, track.name, track.artistName)

    private companion object {
        // The first rows are always on screen; the later ones may be below the fold of a small emulator.
        const val VISIBLE_ROWS = 3
    }
}
