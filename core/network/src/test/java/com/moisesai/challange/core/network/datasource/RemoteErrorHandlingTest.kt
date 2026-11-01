package com.moisesai.challange.core.network.datasource

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.network.support.createTestApi
import com.moisesai.challange.core.network.support.jsonResponse
import com.moisesai.challange.core.network.support.responseJson
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** What each kind of failure looks like to a datasource, over the real Retrofit, OkHttp and JSON stack. */
class RemoteErrorHandlingTest {

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
    fun `an http error status is a network error`() = runBlocking {
        // Given
        server.enqueue(MockResponse.Builder().code(500).body("oops").build())

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Network)
    }

    @Test
    fun `an unreachable server is a network error`() = runBlocking {
        // Given
        server.close()

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Network)
    }

    @Test
    fun `a malformed body is an unknown error`() = runBlocking {
        // Given
        server.enqueue(jsonResponse("this is not json"))

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `a track missing a required field is an unknown error`() = runBlocking {
        // Given
        server.enqueue(jsonResponse(responseJson("""{"wrapperType":"track","trackId":7}""")))

        // When
        val error = dataSource.getTrack(trackId = 7).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }
}
