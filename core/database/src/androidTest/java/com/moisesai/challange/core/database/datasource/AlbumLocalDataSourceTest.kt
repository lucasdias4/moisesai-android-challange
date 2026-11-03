package com.moisesai.challange.core.database.datasource

import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.AlbumDao
import com.moisesai.challange.core.database.entity.DbAlbumTrack
import com.moisesai.challange.core.database.support.aDbAlbum
import com.moisesai.challange.core.database.support.aDbTrack
import com.moisesai.challange.core.database.support.createInMemoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AlbumLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var dataSource: AlbumLocalDataSource

    private val album = aDbAlbum(id = 100)

    // Ids out of order, so the test shows the album order and not the id order.
    private val tracksInAlbumOrder = listOf(aDbTrack(id = 3), aDbTrack(id = 1), aDbTrack(id = 2))

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dataSource = AlbumLocalDataSource(database, database.albumDao(), database.trackDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun GIVEN_a_saved_album_WHEN_getting_it_THEN_its_names_and_cover_are_returned() = runTest {
        // Given
        dataSource.saveAlbum(album, tracksInAlbumOrder)

        // When
        val saved = dataSource.getAlbum(album.id).getOrThrow()

        // Then
        assertEquals(album, saved)
    }

    @Test
    fun GIVEN_an_album_that_was_never_saved_WHEN_getting_it_THEN_the_result_is_null() = runTest {
        // Given
        dataSource.saveAlbum(album, tracksInAlbumOrder)

        // When
        val unknown = dataSource.getAlbum(albumId = 999).getOrThrow()

        // Then
        assertNull(unknown)
    }

    @Test
    fun GIVEN_a_saved_album_of_three_tracks_WHEN_getting_its_tracks_THEN_they_come_in_the_album_order() = runTest {
        // Given
        dataSource.saveAlbum(album, tracksInAlbumOrder)

        // When
        val tracks = dataSource.getAlbumTracks(album.id).getOrThrow()

        // Then
        assertEquals(tracksInAlbumOrder, tracks)
    }

    @Test
    fun GIVEN_an_album_that_was_never_saved_WHEN_getting_its_tracks_THEN_the_list_is_empty() = runTest {
        // Given
        dataSource.saveAlbum(album, tracksInAlbumOrder)

        // When
        val tracks = dataSource.getAlbumTracks(albumId = 999).getOrThrow()

        // Then
        assertTrue(tracks.isEmpty())
    }

    @Test
    fun GIVEN_an_album_without_tracks_WHEN_saving_and_getting_it_THEN_the_album_is_there_with_no_tracks() = runTest {
        // Given
        dataSource.saveAlbum(album, tracks = emptyList())

        // When
        val saved = dataSource.getAlbum(album.id).getOrThrow()
        val tracks = dataSource.getAlbumTracks(album.id).getOrThrow()

        // Then
        assertEquals(album, saved)
        assertTrue(tracks.isEmpty())
    }

    @Test
    fun GIVEN_a_saved_album_WHEN_saving_it_again_with_other_tracks_THEN_only_the_new_tracks_remain() = runTest {
        // Given
        dataSource.saveAlbum(album, tracksInAlbumOrder)
        val newTracks = listOf(aDbTrack(id = 7), aDbTrack(id = 8))

        // When
        dataSource.saveAlbum(album, newTracks)

        // Then
        assertEquals(newTracks, dataSource.getAlbumTracks(album.id).getOrThrow())
    }

    @Test
    fun GIVEN_two_albums_that_share_a_track_WHEN_saving_the_second_THEN_the_first_keeps_its_tracks() = runTest {
        // Given
        val firstAlbum = aDbAlbum(id = 100)
        val secondAlbum = aDbAlbum(id = 200)
        val sharedTrack = aDbTrack(id = 1)
        dataSource.saveAlbum(firstAlbum, listOf(sharedTrack, aDbTrack(id = 2)))

        // When
        dataSource.saveAlbum(secondAlbum, listOf(sharedTrack))

        // Then
        assertEquals(listOf(sharedTrack, aDbTrack(id = 2)), dataSource.getAlbumTracks(firstAlbum.id).getOrThrow())
        assertEquals(listOf(sharedTrack), dataSource.getAlbumTracks(secondAlbum.id).getOrThrow())
    }

    @Test
    fun GIVEN_a_saved_album_and_a_failing_insert_WHEN_saving_it_again_THEN_the_earlier_tracks_are_kept() = runTest {
        // Given
        val earlierTracks = tracksInAlbumOrder.take(2)
        dataSource.saveAlbum(album, earlierTracks)
        val failingDao = object : AlbumDao by database.albumDao() {
            override suspend fun insertAlbumTracks(albumTracks: List<DbAlbumTrack>) {
                throw IllegalStateException("insert failed")
            }
        }
        val failingDataSource = AlbumLocalDataSource(database, failingDao, database.trackDao())

        // When
        val result = failingDataSource.saveAlbum(album, listOf(aDbTrack(id = 7)))

        // Then
        assertTrue(result.isFailure)
        assertEquals(earlierTracks, dataSource.getAlbumTracks(album.id).getOrThrow())
    }
}
