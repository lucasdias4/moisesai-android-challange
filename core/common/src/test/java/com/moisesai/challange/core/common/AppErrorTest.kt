package com.moisesai.challange.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class AppErrorTest {

    @Test
    fun `GIVEN a network error built with a cause, WHEN reading its cause, THEN it is the same cause`() {
        // Given
        val cause = IllegalStateException("socket closed")

        // When
        val error = AppError.Network(cause)

        // Then
        assertSame(cause, error.cause)
    }

    @Test
    fun `GIVEN a network error built without a cause, WHEN reading it, THEN it has the network message and no cause`() {
        // Given
        val error = AppError.Network()

        // When
        val message = error.message

        // Then
        assertEquals("Network error", message)
        assertNull(error.cause)
    }

    @Test
    fun `GIVEN an unknown error built with a cause, WHEN reading its cause, THEN it is the same cause`() {
        // Given
        val cause = IllegalArgumentException("bad json")

        // When
        val error = AppError.Unknown(cause)

        // Then
        assertSame(cause, error.cause)
    }

    @Test
    fun `GIVEN an unknown error built without a cause, WHEN reading it, THEN it has the unknown message and no cause`() {
        // Given
        val error = AppError.Unknown()

        // When
        val message = error.message

        // Then
        assertEquals("Unknown error", message)
        assertNull(error.cause)
    }
}
