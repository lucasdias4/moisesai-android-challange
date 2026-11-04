package com.moisesai.challange.e2e

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moisesai.challange.core.strings.R
import com.moisesai.challange.e2e.support.EndToEndTest
import com.moisesai.challange.e2e.support.FakeBackend
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** The journeys that matter most, each one through the whole app: splash, screens, database, backend and player. */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainJourneysTest : EndToEndTest() {

    private val searchTracks = FakeBackend.fixture(SEARCH_FIXTURE).getJSONArray("results")
    private val firstTrack = searchTracks.getJSONObject(0)
    // The app shows the title without the "(Live)" kind of suffix.
    private val searchedTrackTitle = firstTrack.getString("trackName").substringBefore(" (")

    @Test
    fun GIVEN_a_new_install_WHEN_the_app_opens_THEN_the_splash_leads_to_suggested_songs() {
        waitForTextContaining(SUGGESTED)

        assertTrue(countTextContaining(SUGGESTED) > 1)
        assertTrue(FakeBackend.receivedCalls.any { it.startsWith("/lookup") })
    }

    @Test
    fun GIVEN_the_suggested_songs_WHEN_the_user_opens_one_THEN_it_plays_and_can_be_paused() {
        waitForTextContaining(SUGGESTED)

        composeRule.onAllNodesWithText(SUGGESTED, substring = true).onFirst().performClick()

        waitForDescription(text(R.string.songdetails_pause))
        assertEquals(listOf(firstTrack.getString("previewUrl")), player.playedAudioUrls)
        composeRule.onNodeWithContentDescription(text(R.string.songdetails_pause)).performClick()
        waitForDescription(text(R.string.songdetails_play))
    }

    @Test
    fun GIVEN_a_song_was_played_WHEN_the_user_goes_back_THEN_the_home_shows_only_what_was_played() {
        waitForTextContaining(SUGGESTED)
        composeRule.onAllNodesWithText(SUGGESTED, substring = true).onFirst().performClick()
        waitForDescription(text(R.string.songdetails_pause))

        composeRule.onNodeWithContentDescription(text(R.string.common_back)).performClick()

        composeRule.waitUntil(TIMEOUT_MILLIS) { countTextContaining(SUGGESTED) == 1 }
    }

    @Test
    fun GIVEN_a_song_in_the_list_WHEN_the_user_opens_its_album_and_taps_an_album_song_THEN_that_song_plays() {
        waitForTextContaining(SUGGESTED)
        val albumTrack = FakeBackend.fixture(ALBUM_FIXTURE).getJSONArray("results").getJSONObject(1)

        composeRule.onAllNodesWithContentDescription(text(R.string.common_more_options)).onFirst().performClick()
        composeRule.onNodeWithText(text(R.string.songs_view_album)).performClick()
        waitForText(albumTrack.getString("trackName"))
        composeRule.onAllNodesWithText(albumTrack.getString("trackName")).onFirst().performClick()

        waitForDescription(text(R.string.songdetails_pause))
        assertEquals(albumTrack.getString("previewUrl"), player.playedAudioUrls.last())
    }

    @Test
    fun GIVEN_a_search_without_network_WHEN_the_network_returns_and_the_user_retries_THEN_the_results_appear() {
        waitForTextContaining(SUGGESTED)
        FakeBackend.isOffline = true

        composeRule.onNode(hasSetTextAction()).performTextInput("willie")
        waitForText(text(R.string.songs_error_network_title))
        FakeBackend.isOffline = false
        composeRule.onNodeWithText(text(R.string.songs_retry)).performClick()

        waitForTextContaining(searchedTrackTitle)
        composeRule.onAllNodesWithText(text(R.string.songs_error_network_title)).assertCountEquals(0)
    }

    @Test
    fun GIVEN_a_search_with_no_match_WHEN_the_user_submits_it_THEN_the_message_shows_and_the_magnifier_brings_the_field_back() {
        waitForTextContaining(SUGGESTED)

        composeRule.onNode(hasSetTextAction()).performTextInput(FakeBackend.NO_RESULTS_TERM)
        waitForText(text(R.string.songs_no_results_title))
        composeRule.onNode(hasSetTextAction()).performImeAction()
        composeRule.onNodeWithContentDescription(text(R.string.songs_search_button)).performClick()

        composeRule.onNode(hasSetTextAction()).assertIsDisplayed()
    }

    private companion object {
        const val SUGGESTED = FakeBackend.SUGGESTED_NAME_PREFIX
        const val SEARCH_FIXTURE = "search_songs.json"
        const val ALBUM_FIXTURE = "lookup_album.json"
    }
}
