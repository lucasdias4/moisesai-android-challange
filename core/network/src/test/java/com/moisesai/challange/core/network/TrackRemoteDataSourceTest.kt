package com.moisesai.challange.core.network

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.tracks
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
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
    fun `search maps the live response and sends the expected query`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("search_songs.json")))

        val tracks = dataSource.searchTracksBySearchText("jack johnson", MAX_RESULTS).getOrThrow().tracks()

        val request = server.takeRequest()
        assertEquals("/search", request.url.encodedPath)
        assertEquals("jack johnson", request.url.queryParameter("term"))
        assertEquals("music", request.url.queryParameter("media"))
        assertEquals("song", request.url.queryParameter("entity"))
        assertEquals(MAX_RESULTS.toString(), request.url.queryParameter("limit"))
        assertEquals(3, tracks.size)
        with(tracks.first()) {
            assertEquals(6828811031, id)
            assertEquals("On The Road Again (Live)", name)
            assertEquals(6828810178, albumId)
            assertEquals(156629L, trackTimeMillis)
            assertTrue(previewUrl!!.startsWith("https://audio-ssl.itunes.apple.com/"))
            assertTrue(artUrl!!.endsWith("100x100bb.jpg"))
        }
    }

    @Test
    fun `an empty result is a success with an empty list, not an error`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("empty.json")))

        val response = dataSource.searchTracksBySearchText("zzzz", limit = 50).getOrThrow()

        assertTrue(response.results.isEmpty())
    }

    @Test
    fun `optional fields become null`() = runBlocking {
        server.enqueue(
            jsonResponse(
                """{"results":[
                    {"wrapperType":"track","trackId":7,"trackName":"Song","artistName":"Artist",
                     "collectionId":9,"collectionName":"Album","trackTimeMillis":1000,"trackNumber":1}
                ]}""",
            ),
        )

        val track = dataSource.searchTracksBySearchText("x", limit = 10).getOrThrow().tracks().single()

        assertEquals(7L, track.id)
        assertNull(track.previewUrl)
        assertNull(track.artUrl)
        assertNull(track.releaseDate)
    }

    @Test
    fun `a track without a required field is an unknown error`() = runBlocking {
        server.enqueue(jsonResponse("""{"results":[{"wrapperType":"track","trackId":7}]}"""))

        val error = dataSource.searchTracksBySearchText("x", limit = 10).exceptionOrNull()

        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `getting a track returns it`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("lookup_track.json")))

        val track = dataSource.getTrack(6828811031).getOrThrow().tracks().single()

        assertEquals(6828811031, track.id)
    }

    @Test
    fun `getting an unknown track has no results`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("empty.json")))

        val response = dataSource.getTrack(1).getOrThrow()

        assertTrue(response.results.isEmpty())
    }

    @Test
    fun `an http error is a network error`() = runBlocking {
        server.enqueue(MockResponse.Builder().code(500).body("oops").build())

        val error = dataSource.searchTracksBySearchText("x", limit = 10).exceptionOrNull()

        assertTrue(error is AppError.Network)
    }

    @Test
    fun `a dropped connection is a network error`() = runBlocking {
        server.enqueue(MockResponse.Builder().code(200).body("{").build())
        server.close()

        val error = dataSource.searchTracksBySearchText("x", limit = 10).exceptionOrNull()

        assertTrue(error is AppError.Network)
    }

    @Test
    fun `a malformed body is an unknown error`() = runBlocking {
        server.enqueue(jsonResponse("this is not json"))

        val error = dataSource.searchTracksBySearchText("x", limit = 10).exceptionOrNull()

        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `a body without results is an unknown error, not an empty search`() = runBlocking {
        server.enqueue(jsonResponse("{}"))

        val error = dataSource.searchTracksBySearchText("x", limit = 10).exceptionOrNull()

        assertTrue(error is AppError.Unknown)
    }

    private companion object {
        const val MAX_RESULTS = 200
    }
}
