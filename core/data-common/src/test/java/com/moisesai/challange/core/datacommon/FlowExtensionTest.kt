package com.moisesai.challange.core.datacommon

import app.cash.turbine.test
import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowExtensionTest {

    @Test
    fun `GIVEN a flow of values, WHEN converting it to results, THEN each value is a success and the flow completes`() = runTest {
        // Given
        val values = flowOf(1, 2)

        // When
        val results = values.asSafeResult()

        // Then
        results.test {
            assertEquals(1, awaitItem().getOrThrow())
            assertEquals(2, awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a flow that fails after a value, WHEN converting it to results, THEN the failure is emitted and the flow completes`() =
        runTest {
            // Given
            val failing = flow {
                emit(1)
                throw IllegalStateException("query failed")
            }

            // When
            val results = failing.asSafeResult()

            // Then
            results.test {
                assertEquals(1, awaitItem().getOrThrow())
                assertTrue(awaitItem().exceptionOrNull() is AppError.Unknown)
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN a flow that fails and a custom mapper, WHEN converting it to results, THEN the custom mapper makes the failure`() = runTest {
        // Given
        val failing = flow<Int> { throw IllegalStateException("query failed") }
        val customError = AppError.Network()

        // When
        val results = failing.asSafeResult(mapError = { customError })

        // Then
        results.test {
            assertSame(customError, awaitItem().exceptionOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a flow that is cancelled and a custom mapper, WHEN converting it to results, THEN the cancellation is rethrown and never emitted`() {
        // Given
        val cancelled = flow<Int> { throw CancellationException("cancelled") }

        // When
        val collect = { runBlocking { cancelled.asSafeResult(mapError = { AppError.Unknown(it) }).toList() } }

        // Then
        assertThrows(CancellationException::class.java) { collect() }
    }
}
