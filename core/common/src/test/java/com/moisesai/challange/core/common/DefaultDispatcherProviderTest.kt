package com.moisesai.challange.core.common

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DefaultDispatcherProviderTest {

    @Test
    fun `GIVEN the default provider, WHEN asking for the io dispatcher, THEN it is Dispatchers IO`() {
        // Given
        val provider: DispatcherProvider = DefaultDispatcherProvider

        // When
        val dispatcher = provider.io

        // Then
        assertSame(Dispatchers.IO, dispatcher)
    }

    @Test
    fun `GIVEN the default provider, WHEN asking for the default dispatcher, THEN it is Dispatchers Default`() {
        // Given
        val provider: DispatcherProvider = DefaultDispatcherProvider

        // When
        val dispatcher = provider.default

        // Then
        assertSame(Dispatchers.Default, dispatcher)
    }
}
