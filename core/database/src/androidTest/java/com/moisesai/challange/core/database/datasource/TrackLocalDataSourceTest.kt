package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.createInMemoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TrackLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var dataSource: TrackLocalDataSource

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dataSource = TrackLocalDataSource(database.trackDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun GIVEN_a_saved_track_WHEN_getting_it_THEN_every_field_is_returned() = runTest {
        // Given
        val track = aDbTrack(id = 7)
        dataSource.saveTrack(track)

        // When
        val saved = dataSource.getTrack(trackId = 7).getOrThrow()

        // Then
        assertEquals(track, saved)
    }

    @Test
    fun GIVEN_a_saved_track_without_optional_fields_WHEN_getting_it_THEN_the_optional_fields_are_null() = runTest {
        // Given
        val track = aDbTrack(id = 7).copy(artUrl = null, previewUrl = null, releaseDate = null)
        dataSource.saveTrack(track)

        // When
        val saved = dataSource.getTrack(trackId = 7).getOrThrow()

        // Then
        assertEquals(track, saved)
    }

    @Test
    fun GIVEN_no_saved_track_WHEN_getting_an_unknown_id_THEN_the_result_is_null() = runTest {
        // Given
        dataSource.saveTrack(aDbTrack(id = 7))

        // When
        val unknown = dataSource.getTrack(trackId = 8).getOrThrow()

        // Then
        assertNull(unknown)
    }

    @Test
    fun GIVEN_a_saved_track_WHEN_saving_the_same_id_with_new_values_THEN_the_track_is_updated() = runTest {
        // Given
        dataSource.saveTrack(aDbTrack(id = 7))
        val updated = aDbTrack(id = 7).copy(name = "Renamed")

        // When
        dataSource.saveTrack(updated)

        // Then
        assertEquals(updated, dataSource.getTrack(trackId = 7).getOrThrow())
    }
}
