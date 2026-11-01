package com.moisesai.challange.core.network.support

import com.moisesai.challange.core.network.api.ItunesApi
import com.moisesai.challange.core.network.api.ItunesJson
import com.moisesai.challange.core.network.api.createItunesApi
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient

internal fun createTestApi(server: MockWebServer): ItunesApi =
    createItunesApi(server.url("/").toString(), OkHttpClient(), ItunesJson)

// iTunes answers with `text/javascript`, which the parser must accept.
internal fun jsonResponse(body: String): MockResponse = MockResponse.Builder()
    .code(200)
    .addHeader("Content-Type", "text/javascript; charset=utf-8")
    .body(body)
    .build()
