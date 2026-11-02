package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.network.extension.getOnlyAlbum
import com.moisesai.challange.core.network.extension.getOnlyTracks
import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.core.network.support.Fixtures
import com.moisesai.challange.core.network.support.createTestApi
import com.moisesai.challange.core.network.support.jsonResponse
import com.moisesai.challange.core.network.support.readFixture
import com.moisesai.challange.core.network.support.responseJson
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class AlbumRemoteDataSourceTest {

    private val server = MockWebServer()
    private lateinit var dataSource: AlbumRemoteDataSource

    @Before
    fun setUp() {
        server.start()
        dataSource = AlbumRemoteDataSource(createTestApi(server))
    }

    @After
    fun tearDown() = server.close()

    @Test
    fun `GIVEN an album id, WHEN getting the album with its tracks, THEN the lookup endpoint is called with the id and the song entity`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        dataSource.getAlbumWithTracks(albumId = 6828810178)

        // Then
        val request = server.takeRequest()
        assertEquals("/lookup", request.url.encodedPath)
        assertEquals("6828810178", request.url.queryParameter("id"))
        assertEquals("song", request.url.queryParameter("entity"))
    }

    @Test
    fun `GIVEN a real album response, WHEN getting the album with its tracks, THEN the album has every field mapped`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.LOOKUP_ALBUM)))

        // When
        val album = dataSource.getAlbumWithTracks(albumId = 6828810178).getOrThrow().getOnlyAlbum()!!

        // Then
        assertEquals(6828810178, album.id)
        assertEquals("Willie Nelson American Outlaw (Live)", album.name)
        assertEquals("Willie Nelson", album.artistName)
        assertTrue(album.artUrl!!.endsWith("100x100bb.jpg"))
        assertEquals("2020-12-11T08:00:00Z", album.releaseDate)
        assertEquals(24, album.trackCount)
    }

    @Test
    fun `GIVEN a real album response, WHEN getting the album with its tracks, THEN the tracks follow the album in track order`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.LOOKUP_ALBUM)))

        // When
        val response = dataSource.getAlbumWithTracks(albumId = 6828810178).getOrThrow()

        // Then
        val kinds: List<KClass<*>> = response.results.map { it::class }
        assertEquals(listOf(ApiAlbum::class, ApiTrack::class, ApiTrack::class, ApiTrack::class), kinds)
        assertEquals(listOf(1, 2, 3), response.getOnlyTracks().map { it.trackNumber })
    }

    @Test
    fun `GIVEN an unknown album id, WHEN getting the album with its tracks, THEN the response is successful and has no results`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        val response = dataSource.getAlbumWithTracks(albumId = 1).getOrThrow()

        // Then
        assertTrue(response.results.isEmpty())
    }

    @Test
    fun `GIVEN an album missing a required field, WHEN getting the album with its tracks, THEN the result is an unknown error`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(responseJson("""{"wrapperType":"collection","collectionId":9}""")))

        // When
        val error = dataSource.getAlbumWithTracks(albumId = 9).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }
}
