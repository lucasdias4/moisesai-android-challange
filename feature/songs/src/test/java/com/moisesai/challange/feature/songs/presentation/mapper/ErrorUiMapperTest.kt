package com.moisesai.challange.feature.songs.presentation.mapper

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorUiMapperTest {

    @Test
    fun `GIVEN a network error, WHEN mapped to ui, THEN it is the network ui error`() {
        // Given
        val error: Throwable = AppError.Network()

        // When
        val uiError = error.toUiError()

        // Then
        assertEquals(UiSongsError.Network, uiError)
    }

    @Test
    fun `GIVEN an unknown app error, WHEN mapped to ui, THEN it is the unknown ui error`() {
        // Given
        val error: Throwable = AppError.Unknown()

        // When
        val uiError = error.toUiError()

        // Then
        assertEquals(UiSongsError.Unknown, uiError)
    }

    @Test
    fun `GIVEN any other throwable, WHEN mapped to ui, THEN it is the unknown ui error`() {
        // Given
        val error: Throwable = IllegalStateException()

        // When
        val uiError = error.toUiError()

        // Then
        assertEquals(UiSongsError.Unknown, uiError)
    }
}
