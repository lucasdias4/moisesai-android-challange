package com.moisesai.challange.core.network.extension

import com.moisesai.challange.core.network.model.ApiAlbum
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack

// Public on purpose: the datasources return the raw response, and the repositories of every
// feature use these to read the kinds of item it mixes (a lookup returns the album, then its tracks).
fun ApiResponse.tracks(): List<ApiTrack> = results.filterIsInstance<ApiTrack>()

fun ApiResponse.album(): ApiAlbum? = results.filterIsInstance<ApiAlbum>().firstOrNull()
