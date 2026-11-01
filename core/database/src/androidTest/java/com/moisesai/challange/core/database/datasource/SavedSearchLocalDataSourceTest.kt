package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.SavedSearchDao
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbSavedSearchTrack
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.createInMemoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SavedSearchLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var dataSource: SavedSearchLocalDataSource

    private val fiveTracks = (1L..5L).map { aDbTrack(id = it) }

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dataSource = SavedSearchLocalDataSource(database, database.savedSearchDao(), database.trackDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun GIVEN_a_saved_search_WHEN_getting_it_THEN_its_text_and_fetch_time_are_returned() = runTest {
        // Given
        val search = DbSavedSearch(searchText = "jack johnson", fetchedAtMillis = 1_000)
        dataSource.saveSearch(search, fiveTracks)

        // When
        val saved = dataSource.getSavedSearch("jack johnson").getOrThrow()

        // Then
        assertEquals(search, saved)
    }

    @Test
    fun GIVEN_a_search_that_was_never_saved_WHEN_getting_it_THEN_the_result_is_null() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks)

        // When
        val unknown = dataSource.getSavedSearch("daft punk").getOrThrow()

        // Then
        assertNull(unknown)
    }

    @Test
    fun GIVEN_a_saved_search_of_five_tracks_WHEN_getting_its_first_page_THEN_the_tracks_come_in_the_saved_order() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks)

        // When
        val page = dataSource.getSavedSearchTracksPage("jack johnson", limit = 3, offset = 0).getOrThrow()

        // Then
        assertEquals(fiveTracks.take(3), page)
    }

    @Test
    fun GIVEN_a_saved_search_of_five_tracks_WHEN_getting_its_second_page_THEN_the_next_tracks_are_returned() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks)

        // When
        val page = dataSource.getSavedSearchTracksPage("jack johnson", limit = 3, offset = 3).getOrThrow()

        // Then
        assertEquals(fiveTracks.drop(3), page)
    }

    @Test
    fun GIVEN_a_saved_search_of_five_tracks_WHEN_getting_a_page_past_the_last_track_THEN_the_page_is_empty() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks)

        // When
        val page = dataSource.getSavedSearchTracksPage("jack johnson", limit = 3, offset = 5).getOrThrow()

        // Then
        assertTrue(page.isEmpty())
    }

    @Test
    fun GIVEN_a_search_text_that_was_never_saved_WHEN_getting_its_tracks_page_THEN_the_page_is_empty() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks)

        // When
        val page = dataSource.getSavedSearchTracksPage("daft punk", limit = 3, offset = 0).getOrThrow()

        // Then
        assertTrue(page.isEmpty())
    }

    @Test
    fun GIVEN_two_saved_searches_WHEN_getting_the_page_of_one_THEN_only_its_own_tracks_are_returned() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks.take(2))
        dataSource.saveSearch(DbSavedSearch("daft punk", fetchedAtMillis = 2_000), fiveTracks.drop(2))

        // When
        val page = dataSource.getSavedSearchTracksPage("jack johnson", limit = 10, offset = 0).getOrThrow()

        // Then
        assertEquals(fiveTracks.take(2), page)
    }

    @Test
    fun GIVEN_a_saved_search_WHEN_saving_it_again_with_other_tracks_THEN_only_the_new_tracks_and_fetch_time_remain() = runTest {
        // Given
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), fiveTracks.take(3))
        val refreshed = DbSavedSearch("jack johnson", fetchedAtMillis = 9_000)

        // When
        dataSource.saveSearch(refreshed, fiveTracks.drop(3))

        // Then
        assertEquals(refreshed, dataSource.getSavedSearch("jack johnson").getOrThrow())
        assertEquals(
            fiveTracks.drop(3),
            dataSource.getSavedSearchTracksPage("jack johnson", limit = 10, offset = 0).getOrThrow(),
        )
    }

    @Test
    fun GIVEN_two_saved_searches_sharing_a_track_WHEN_one_is_saved_again_without_it_THEN_the_other_keeps_the_track() = runTest {
        // Given
        val shared = aDbTrack(id = 1)
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), listOf(shared, aDbTrack(id = 2)))
        dataSource.saveSearch(DbSavedSearch("daft punk", fetchedAtMillis = 2_000), listOf(shared))

        // When
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 3_000), listOf(aDbTrack(id = 3)))

        // Then
        val untouched = dataSource.getSavedSearchTracksPage("daft punk", limit = 10, offset = 0).getOrThrow()
        assertEquals(listOf(shared), untouched)
    }

    @Test
    fun GIVEN_a_saved_search_WHEN_another_search_saves_the_same_track_THEN_the_first_search_still_returns_it() = runTest {
        // Given
        val shared = aDbTrack(id = 1)
        dataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), listOf(shared))

        // When
        dataSource.saveSearch(DbSavedSearch("daft punk", fetchedAtMillis = 2_000), listOf(shared))

        // Then
        val firstSearch = dataSource.getSavedSearchTracksPage("jack johnson", limit = 10, offset = 0).getOrThrow()
        assertEquals(listOf(shared), firstSearch)
    }

    @Test
    fun GIVEN_a_saved_search_and_a_failing_insert_WHEN_saving_it_again_THEN_the_earlier_search_is_kept() = runTest {
        // Given
        val earlier = DbSavedSearch("jack johnson", fetchedAtMillis = 1_000)
        dataSource.saveSearch(earlier, fiveTracks.take(2))
        val failingDao = object : SavedSearchDao by database.savedSearchDao() {
            override suspend fun insertSavedSearchTracks(savedSearchTracks: List<DbSavedSearchTrack>) {
                throw IllegalStateException("insert failed")
            }
        }
        val failingDataSource = SavedSearchLocalDataSource(database, failingDao, database.trackDao())

        // When
        val result = failingDataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 9_000), fiveTracks.drop(2))

        // Then
        assertTrue(result.isFailure)
        assertEquals(earlier, dataSource.getSavedSearch("jack johnson").getOrThrow())
        assertEquals(
            fiveTracks.take(2),
            dataSource.getSavedSearchTracksPage("jack johnson", limit = 10, offset = 0).getOrThrow(),
        )
    }
}
