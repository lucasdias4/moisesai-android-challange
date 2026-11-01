package com.moisesai.challange.core.database.extension

import app.cash.turbine.test
import com.moisesai.challange.core.common.AppError
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowResultExtensionTest {

    @Test
    fun `GIVEN a flow of values, WHEN converting it to results, THEN each value is a success and the flow completes`() = runTest {
        // Given
        val values = flowOf(1, 2)

        // When
        val results = values.asResult()

        // Then
        results.test {
            assertEquals(1, awaitItem().getOrThrow())
            assertEquals(2, awaitItem().getOrThrow())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a flow that fails after a value, WHEN converting it to results, THEN the failure is emitted and the flow completes`() = runTest {
        // Given
        val failing = flow {
            emit(1)
            throw IllegalStateException("query failed")
        }

        // When
        val results = failing.asResult()

        // Then
        results.test {
            assertEquals(1, awaitItem().getOrThrow())
            assertTrue(awaitItem().exceptionOrNull() is AppError.Unknown)
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN a flow that is cancelled, WHEN converting it to results, THEN the cancellation is rethrown, not emitted`() {
        // Given
        val cancelled = flow<Int> { throw CancellationException("cancelled") }

        // When
        val collect = { runBlocking { cancelled.asResult().toList() } }

        // Then
        assertThrows(CancellationException::class.java) { collect() }
    }
}
