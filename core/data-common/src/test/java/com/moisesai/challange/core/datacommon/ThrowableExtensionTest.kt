package com.moisesai.challange.core.datacommon

import com.moisesai.challange.core.common.AppError
import java.io.IOException
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ThrowableExtensionTest {

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
    fun `GIVEN an unexpected exception, WHEN converting it to an app error, THEN it is an unknown error that keeps its cause`() {
        // Given
        val unexpected = IllegalStateException("boom")

        // When
        val error = unexpected.toAppError()

        // Then
        assertTrue(error is AppError.Unknown)
        assertSame(unexpected, error.cause)
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
}
