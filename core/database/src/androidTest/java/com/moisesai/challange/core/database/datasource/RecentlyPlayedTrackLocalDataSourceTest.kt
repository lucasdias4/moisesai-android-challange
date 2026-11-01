package com.moisesai.challange.core.database.datasource

import app.cash.turbine.test
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.RecentlyPlayedTrackDao
import com.moisesai.challange.core.database.entity.DbRecentlyPlayedTrack
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.createInMemoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecentlyPlayedTrackLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var dataSource: RecentlyPlayedTrackLocalDataSource

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dataSource = RecentlyPlayedTrackLocalDataSource(database, database.recentlyPlayedTrackDao(), database.trackDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun GIVEN_no_recently_played_track_WHEN_observing_the_recently_played_tracks_THEN_an_empty_list_is_emitted() = runTest {
        // Given
        val limit = 10

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit)

        // Then
        recentTracks.test {
            assertEquals(emptyList<Any>(), awaitItem().getOrThrow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun GIVEN_a_saved_recently_played_track_WHEN_observing_the_recently_played_tracks_THEN_the_track_is_emitted_with_every_field() = runTest {
        // Given
        val track = aDbTrack(id = 7)
        dataSource.saveRecentlyPlayedTrack(track, playedAtMillis = 1_000)

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 10)

        // Then
        recentTracks.test {
            assertEquals(listOf(track), awaitItem().getOrThrow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun GIVEN_three_recently_played_tracks_WHEN_observing_the_recently_played_tracks_THEN_the_latest_played_comes_first() = runTest {
        // Given
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 1), playedAtMillis = 1_000)
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 2), playedAtMillis = 3_000)
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 3), playedAtMillis = 2_000)

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 10)

        // Then
        recentTracks.test {
            assertEquals(listOf(2L, 3L, 1L), awaitItem().getOrThrow().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun GIVEN_more_recently_played_tracks_than_the_limit_WHEN_observing_the_recently_played_tracks_THEN_only_the_newest_up_to_the_limit_are_emitted() = runTest {
        // Given
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 1), playedAtMillis = 1_000)
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 2), playedAtMillis = 2_000)
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 3), playedAtMillis = 3_000)

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 2)

        // Then
        recentTracks.test {
            assertEquals(listOf(3L, 2L), awaitItem().getOrThrow().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun GIVEN_a_recently_played_track_WHEN_it_is_played_again_later_THEN_it_moves_to_the_top_and_is_not_duplicated() = runTest {
        // Given
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 1), playedAtMillis = 1_000)
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 2), playedAtMillis = 2_000)

        // When
        dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 1), playedAtMillis = 3_000)

        // Then
        dataSource.observeRecentlyPlayedTracks(limit = 10).test {
            assertEquals(listOf(1L, 2L), awaitItem().getOrThrow().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun GIVEN_an_observed_list_WHEN_a_new_recently_played_track_is_saved_THEN_a_new_list_is_emitted() = runTest {
        turbineScope {
            // Given
            val observed = dataSource.observeRecentlyPlayedTracks(limit = 10).testIn(backgroundScope)
            observed.awaitItem()
            val track = aDbTrack(id = 7)

            // When
            dataSource.saveRecentlyPlayedTrack(track, playedAtMillis = 1_000)

            // Then
            assertEquals(listOf(track), observed.awaitItem().getOrThrow())
            observed.cancel()
        }
    }

    @Test
    fun GIVEN_a_track_from_a_saved_search_WHEN_it_is_saved_as_a_recently_played_track_THEN_the_search_still_returns_it() = runTest {
        // Given
        val savedSearchDataSource = SavedSearchLocalDataSource(database, database.savedSearchDao(), database.trackDao())
        val track = aDbTrack(id = 7)
        savedSearchDataSource.saveSearch(DbSavedSearch("jack johnson", fetchedAtMillis = 1_000), listOf(track))

        // When
        dataSource.saveRecentlyPlayedTrack(track, playedAtMillis = 2_000)

        // Then
        val page = savedSearchDataSource.getSavedSearchTracksPage("jack johnson", limit = 10, offset = 0).getOrThrow()
        assertEquals(listOf(track), page)
    }

    @Test
    fun GIVEN_a_failing_recently_played_save_WHEN_saving_a_recently_played_track_THEN_the_track_is_not_saved_either() = runTest {
        // Given
        val failingDao = object : RecentlyPlayedTrackDao by database.recentlyPlayedTrackDao() {
            override suspend fun upsertRecentlyPlayedTrack(recentlyPlayedTrack: DbRecentlyPlayedTrack) {
                throw IllegalStateException("upsert failed")
            }
        }
        val failingDataSource = RecentlyPlayedTrackLocalDataSource(database, failingDao, database.trackDao())

        // When
        val result = failingDataSource.saveRecentlyPlayedTrack(aDbTrack(id = 7), playedAtMillis = 1_000)

        // Then
        assertTrue(result.isFailure)
        assertNull(database.trackDao().getTrack(trackId = 7))
    }
}
