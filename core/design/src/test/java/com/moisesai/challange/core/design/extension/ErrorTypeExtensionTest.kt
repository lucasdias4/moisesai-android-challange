package com.moisesai.challange.core.design.extension

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.design.model.UiErrorType
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorTypeExtensionTest {

    @Test
    fun `GIVEN a network error, WHEN mapped to an error type, THEN it is the network type`() {
        // Given
        val error: Throwable = AppError.Network()

        // When
        val type = error.toUiErrorType()

        // Then
        assertEquals(UiErrorType.Network, type)
    }

    @Test
    fun `GIVEN any other throwable, WHEN mapped to an error type, THEN it is the unknown type`() {
        // Given
        val error: Throwable = IllegalStateException()

        // When
        val type = error.toUiErrorType()

        // Then
        assertEquals(UiErrorType.Unknown, type)
    }
}
