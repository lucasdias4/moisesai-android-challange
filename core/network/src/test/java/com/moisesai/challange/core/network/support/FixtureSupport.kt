package com.moisesai.challange.core.network.support

/** Fixtures are real iTunes responses, trimmed only by removing items from a list. */
internal object Fixtures {
    const val SEARCH_SONGS = "search_songs.json"
    const val LOOKUP_TRACK = "lookup_track.json"
    const val LOOKUP_ALBUM = "lookup_album.json"
    const val EMPTY = "empty.json"
}

internal fun readFixture(name: String): String = checkNotNull(Thread.currentThread().contextClassLoader?.getResource("fixtures/$name")) {
    "Missing fixture $name"
}.readText()
