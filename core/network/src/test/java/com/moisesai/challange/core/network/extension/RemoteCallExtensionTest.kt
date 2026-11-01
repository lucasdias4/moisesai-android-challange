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

class RemoteCallExtensionTest {

    @Test
    fun `a returned value is a success`() = runTest {
        // Given
        val request: suspend () -> Int = { 42 }

        // When
        val result = remoteCall(request)

        // Then
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `a thrown error becomes a failure with the mapped error`() = runTest {
        // Given
        val httpError = HttpException(Response.error<Any>(500, "".toResponseBody()))

        // When
        val error = remoteCall { throw httpError }.exceptionOrNull()

        // Then
        assertTrue(error is AppError.Network)
    }

    @Test
    fun `cancellation is rethrown, never returned as a failure`() {
        // Given
        val cancelled: suspend () -> Unit = { throw CancellationException("cancelled") }

        // When
        val call = { runBlocking { remoteCall(cancelled) } }

        // Then
        assertThrows(CancellationException::class.java) { call() }
    }
}
