package com.moisesai.challange.core.network

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.network.extension.remoteCall
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RemoteCallTest {

    @Test
    fun `a returned value is a success`() = runBlocking {
        assertEquals(42, remoteCall { 42 }.getOrThrow())
    }

    @Test
    fun `an io exception is a network error that keeps its cause`() = runBlocking {
        val cause = IOException("offline")

        val error = remoteCall { throw cause }.exceptionOrNull()

        assertTrue(error is AppError.Network)
        assertSame(cause, error?.cause)
    }

    @Test
    fun `an http exception is a network error`() = runBlocking {
        val httpError = HttpException(Response.error<Any>(500, "".toResponseBody()))

        val error = remoteCall { throw httpError }.exceptionOrNull()

        assertTrue(error is AppError.Network)
    }

    @Test
    fun `any other exception is an unknown error`() = runBlocking {
        val error = remoteCall { error("boom") }.exceptionOrNull()

        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `cancellation is rethrown, never returned as a failure`() {
        assertThrows(CancellationException::class.java) {
            runBlocking { remoteCall { throw CancellationException("cancelled") } }
        }
    }
}
