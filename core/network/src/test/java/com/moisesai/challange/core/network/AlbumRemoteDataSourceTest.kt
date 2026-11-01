package com.moisesai.challange.core.network

import com.moisesai.challange.core.network.datasource.AlbumRemoteDataSource
import com.moisesai.challange.core.network.extension.album
import com.moisesai.challange.core.network.extension.tracks
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
    fun `getting an album sends the expected query and returns it with its tracks in order`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("lookup_album.json")))

        val response = dataSource.getAlbumWithTracks(6828810178).getOrThrow()

        val request = server.takeRequest()
        assertEquals("/lookup", request.url.encodedPath)
        assertEquals("6828810178", request.url.queryParameter("id"))
        assertEquals("song", request.url.queryParameter("entity"))
        assertEquals("Willie Nelson American Outlaw (Live)", response.album()?.name)
        assertEquals(24, response.album()?.trackCount)
        assertEquals(listOf(1, 2, 3), response.tracks().map { it.trackNumber })
    }

    @Test
    fun `getting an unknown album has no results`() = runBlocking {
        server.enqueue(jsonResponse(readFixture("empty.json")))

        val response = dataSource.getAlbumWithTracks(1).getOrThrow()

        assertTrue(response.results.isEmpty())
    }
}
