package com.moisesai.challange.core.common

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppErrorMapperTest {

    @Test
    fun `GIVEN an io exception, WHEN converting it to an app error, THEN it is a network error that keeps its cause`() {
        // Given
        val cause = IOException("offline")

        // When
        val error = cause.toAppError()

        // Then
        assertTrue(error is AppError.Network)
        assertSame(cause, error.cause)
    }

    @Test
    fun `GIVEN an unexpected exception, WHEN converting it to an app error, THEN it is an unknown error`() {
        // Given
        val unexpected = IllegalStateException("boom")

        // When
        val error = unexpected.toAppError()

        // Then
        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `GIVEN an app error, WHEN converting it to an app error, THEN the same error is returned`() {
        // Given
        val appError = AppError.Network()

        // When
        val converted = appError.toAppError()

        // Then
        assertSame(appError, converted)
    }

    @Test
    fun `GIVEN a cancellation, WHEN converting it to an app error, THEN it is rethrown, not mapped`() {
        // Given
        val cancellation = CancellationException("cancelled")

        // When
        val call = { cancellation.toAppError() }

        // Then
        val thrown = assertThrows(CancellationException::class.java) { call() }
        assertSame(cancellation, thrown)
    }
}
