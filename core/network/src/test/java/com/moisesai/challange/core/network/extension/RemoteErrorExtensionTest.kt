package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.common.AppError
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RemoteErrorExtensionTest {

    @Test
    fun `an http error is a network error that keeps its cause`() {
        // Given
        val httpError = HttpException(Response.error<Any>(500, "".toResponseBody()))

        // When
        val error = httpError.toRemoteError()

        // Then
        assertTrue(error is AppError.Network)
        assertSame(httpError, error.cause)
    }

    @Test
    fun `any other error is classified by toAppError`() {
        // Given
        val unexpected = IllegalStateException("boom")

        // When
        val error = unexpected.toRemoteError()

        // Then
        assertTrue(error is AppError.Unknown)
    }
}
