package com.moisesai.challange.core.database.datasource

import app.cash.turbine.test
import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.createInMemoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** What a database that cannot be used looks like to a datasource, on the real SQLite. */
class LocalErrorHandlingTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
    }

    @Test
    fun GIVEN_a_closed_database_WHEN_getting_a_track_THEN_the_result_is_an_unknown_error() = runTest {
        // Given
        val dataSource = TrackLocalDataSource(database.trackDao())
        database.close()

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun GIVEN_a_closed_database_WHEN_saving_a_recently_played_track_THEN_the_result_is_an_unknown_error() = runTest {
        // Given
        val dataSource = RecentlyPlayedTrackLocalDataSource(database, database.recentlyPlayedTrackDao(), database.trackDao())
        database.close()

        // When
        val error = dataSource.saveRecentlyPlayedTrack(aDbTrack(id = 7), playedAtMillis = 1_000).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun GIVEN_a_closed_database_WHEN_observing_the_recently_played_tracks_THEN_a_failure_is_emitted_instead_of_an_exception() = runTest {
        // Given
        val dataSource = RecentlyPlayedTrackLocalDataSource(database, database.recentlyPlayedTrackDao(), database.trackDao())
        database.close()

        // When
        val recentTracks = dataSource.observeRecentlyPlayedTracks(limit = 10)

        // Then
        recentTracks.test {
            assertTrue(awaitItem().exceptionOrNull() is AppError.Unknown)
            awaitComplete()
        }
    }
}
