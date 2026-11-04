package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SafeRemoteCallExtensionTest {

    @Test
    fun `GIVEN a request that returns a value, WHEN making the remote call, THEN the result is a success with that value`() = runTest {
        // Given
        val request: suspend () -> Int = { 42 }

        // When
        val result = safeRemoteCall(request)

        // Then
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `GIVEN a request that throws an http error, WHEN making the remote call, THEN the result is a failure with a network error`() =
        runTest {
            // Given
            val httpError = HttpException(Response.error<Any>(500, "".toResponseBody()))

            // When
            val error = safeRemoteCall { throw httpError }.exceptionOrNull()

            // Then
            assertTrue(error is AppError.Network)
        }

    @Test
    fun `GIVEN a request that is cancelled, WHEN making the remote call, THEN the cancellation is rethrown, not returned as a failure`() {
        // Given
        val cancelled: suspend () -> Unit = { throw CancellationException("cancelled") }

        // When
        val call = { runBlocking { safeRemoteCall(cancelled) } }

        // Then
        assertThrows(CancellationException::class.java) { call() }
    }
}
