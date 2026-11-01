package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.network.extension.tracks
import com.moisesai.challange.core.network.model.ApiUnknownItem
import com.moisesai.challange.core.network.support.Fixtures
import com.moisesai.challange.core.network.support.createTestApi
import com.moisesai.challange.core.network.support.jsonResponse
import com.moisesai.challange.core.network.support.readFixture
import com.moisesai.challange.core.network.support.responseJson
import com.moisesai.challange.core.network.support.trackJson
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TrackRemoteDataSourceTest {

    private val server = MockWebServer()
    private lateinit var dataSource: TrackRemoteDataSource

    @Before
    fun setUp() {
        server.start()
        dataSource = TrackRemoteDataSource(createTestApi(server))
    }

    @After
    fun tearDown() = server.close()

    @Test
    fun `searching sends the text and limit to the search endpoint filtered to music songs`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        dataSource.searchTracksBySearchText(searchText = "jack johnson", limit = 50)

        // Then
        val request = server.takeRequest()
        assertEquals("/search", request.url.encodedPath)
        assertEquals("jack johnson", request.url.queryParameter("term"))
        assertEquals("50", request.url.queryParameter("limit"))
        assertEquals("music", request.url.queryParameter("media"))
        assertEquals("song", request.url.queryParameter("entity"))
    }

    @Test
    fun `searching returns every field of a real track`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.SEARCH_SONGS)))

        // When
        val track = dataSource.searchTracksBySearchText(searchText = "jack johnson", limit = 3)
            .getOrThrow().tracks().first()

        // Then
        assertEquals(6828811031, track.id)
        assertEquals("On The Road Again (Live)", track.name)
        assertTrue(track.artistName.startsWith("Willie Nelson, Jamey Johnson"))
        assertEquals(6828810178, track.albumId)
        assertEquals("Willie Nelson American Outlaw (Live)", track.albumName)
        assertTrue(track.artUrl!!.endsWith("100x100bb.jpg"))
        assertTrue(track.previewUrl!!.startsWith("https://audio-ssl.itunes.apple.com/"))
        assertEquals(156629L, track.trackTimeMillis)
        assertEquals("2020-12-11T08:00:00Z", track.releaseDate)
        assertEquals(23, track.trackNumber)
    }

    @Test
    fun `searching returns the tracks in the order of the response`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.SEARCH_SONGS)))

        // When
        val tracks = dataSource.searchTracksBySearchText(searchText = "jack johnson", limit = 3)
            .getOrThrow().tracks()

        // Then
        assertEquals(listOf(6828811031, 6828811033, 6828810577), tracks.map { it.id })
    }

    @Test
    fun `searching with no match returns a successful response without results`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        val response = dataSource.searchTracksBySearchText(searchText = "zzzz", limit = 50).getOrThrow()

        // Then
        assertTrue(response.results.isEmpty())
    }

    @Test
    fun `a track without optional fields has them as null`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(responseJson(trackJson())))

        // When
        val track = dataSource.searchTracksBySearchText(searchText = "x", limit = 1).getOrThrow().tracks().single()

        // Then
        assertNull(track.artUrl)
        assertNull(track.previewUrl)
        assertNull(track.releaseDate)
    }

    @Test
    fun `an item of an unknown kind is returned as an unknown item`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(responseJson("""{"wrapperType":"artist","artistName":"Someone"}""")))

        // When
        val response = dataSource.searchTracksBySearchText(searchText = "x", limit = 1).getOrThrow()

        // Then
        assertEquals(ApiUnknownItem, response.results.single())
    }

    @Test
    fun `getting a track sends its id to the lookup endpoint without an entity filter`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        dataSource.getTrack(trackId = 6828811031)

        // Then
        val request = server.takeRequest()
        assertEquals("/lookup", request.url.encodedPath)
        assertEquals("6828811031", request.url.queryParameter("id"))
        assertNull(request.url.queryParameter("entity"))
    }

    @Test
    fun `getting a track returns that track`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.LOOKUP_TRACK)))

        // When
        val track = dataSource.getTrack(trackId = 6828811031).getOrThrow().tracks().single()

        // Then
        assertEquals(6828811031, track.id)
    }

    @Test
    fun `getting an unknown track returns a successful response without results`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

        // When
        val response = dataSource.getTrack(trackId = 1).getOrThrow()

        // Then
        assertTrue(response.results.isEmpty())
    }
}
