package com.moisesai.challange.core.common

import org.junit.Assert.assertTrue
import org.junit.Test

class SystemTimeProviderTest {

    @Test
    fun `GIVEN the system time provider, WHEN reading the time, THEN it is the system clock at that moment`() {
        // Given
        val before = System.currentTimeMillis()

        // When
        val now = SystemTimeProvider.currentTimeMillis()

        // Then
        val after = System.currentTimeMillis()
        assertTrue(now in before..after)
    }
}
