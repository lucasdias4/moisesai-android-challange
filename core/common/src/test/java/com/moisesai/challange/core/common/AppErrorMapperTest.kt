package com.moisesai.challange.core.common

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppErrorMapperTest {

    @Test
    fun `io exception becomes network error keeping the cause`() {
        val cause = IOException("offline")

        val error = cause.toAppError()

        assertTrue(error is AppError.Network)
        assertSame(cause, error.cause)
    }

    @Test
    fun `other exception becomes unknown error`() {
        assertTrue(IllegalStateException("boom").toAppError() is AppError.Unknown)
    }

    @Test
    fun `app error is returned unchanged`() {
        val error = AppError.Network()

        assertSame(error, error.toAppError())
    }

    @Test
    fun `cancellation is rethrown, not mapped`() {
        val cancellation = CancellationException("cancelled")

        val thrown = assertThrows(CancellationException::class.java) { cancellation.toAppError() }

        assertSame(cancellation, thrown)
    }
}
