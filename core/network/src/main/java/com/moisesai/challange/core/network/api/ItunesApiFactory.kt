package com.moisesai.challange.core.network.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val JSON_MEDIA_TYPE = "application/json"

/**
 * [clientProvider] is read when the first request is made, not when the API is built: creating an [OkHttpClient]
 * takes hundreds of milliseconds (it builds the SSL context), and the API is built on the main thread.
 */
internal fun createItunesApi(baseUrl: String, clientProvider: () -> OkHttpClient, json: Json): ItunesApi = Retrofit.Builder()
    .baseUrl(baseUrl)
    .callFactory { request -> clientProvider().newCall(request) }
    .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
    .build()
    .create(ItunesApi::class.java)
