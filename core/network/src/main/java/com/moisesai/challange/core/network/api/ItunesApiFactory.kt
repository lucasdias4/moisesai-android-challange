package com.moisesai.challange.core.network.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val JSON_MEDIA_TYPE = "application/json"

internal fun createItunesApi(baseUrl: String, client: OkHttpClient, json: Json): ItunesApi =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
        .build()
        .create(ItunesApi::class.java)
