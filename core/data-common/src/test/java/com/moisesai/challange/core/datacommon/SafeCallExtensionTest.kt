package com.moisesai.challange.core.datacommon

import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeCallExtensionTest {

    @Test
    fun `GIVEN a block that returns a value, WHEN taking its result, THEN the result is a success with that value`() = runTest {
        // Given
        val block: suspend () -> Int = { 42 }

        // When
        val result = safeCall(block = block)

        // Then
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `GIVEN a block that throws, WHEN taking its result, THEN the default mapper makes it a failure`() = runTest {
        // Given
        val block: suspend () -> Int = { throw IllegalStateException("boom") }

        // When
        val error = safeCall(block = block).exceptionOrNull()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN a block that throws and a custom mapper, WHEN taking its result, THEN the custom mapper makes the failure`() = runTest {
        // Given
        val block: suspend () -> Int = { throw IllegalStateException("boom") }
        val customError = AppError.Network()

        // When
        val error = safeCall(mapError = { customError }, block = block).exceptionOrNull()

        // Then
        assertSame(customError, error)
    }

    @Test
    fun `GIVEN a block that is cancelled and a custom mapper, WHEN taking its result, THEN the cancellation is rethrown and never mapped`() {
        // Given
        val cancelled: suspend () -> Unit = { throw CancellationException("cancelled") }

        // When
        val call = { runBlocking { safeCall(mapError = { AppError.Unknown(it) }, block = cancelled) } }

        // Then
        assertThrows(CancellationException::class.java) { call() }
    }
}
