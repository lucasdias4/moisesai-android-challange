package com.moisesai.challange.core.database.extension

import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalCallExtensionTest {

    @Test
    fun `GIVEN a request that returns a value, WHEN making the local call, THEN the result is a success with that value`() = runTest {
        // Given
        val request: suspend () -> Int = { 42 }

        // When
        val result = localCall(request)

        // Then
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `GIVEN a request that throws, WHEN making the local call, THEN the result is a failure with an unknown error`() = runTest {
        // Given
        val request: suspend () -> Int = { throw IllegalStateException("database closed") }

        // When
        val error = localCall(request).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN a request that is cancelled, WHEN making the local call, THEN the cancellation is rethrown, not returned as a failure`() {
        // Given
        val cancelled: suspend () -> Unit = { throw CancellationException("cancelled") }

        // When
        val call = { runBlocking { localCall(cancelled) } }

        // Then
        assertThrows(CancellationException::class.java) { call() }
    }
}
