package com.moisesai.challange.e2e.support

import androidx.test.platform.app.InstrumentationRegistry
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * A server that answers like iTunes, with the answers recorded from the real API (the fixtures of :core:network).
 * A search for [NO_RESULTS_TERM] finds nothing, and [isOffline] makes every call fail with a 503, which the app shows as a network error.
 * The suggested songs of the first launch are looked up by ids the fixtures do not hold, so each one is answered with
 * a copy of the first fixture song named "[SUGGESTED_NAME_PREFIX] <id>".
 */
object FakeBackend {
    const val NO_RESULTS_TERM = "zzzz"
    const val SUGGESTED_NAME_PREFIX = "Suggested song"
    private const val SEARCH_PATH = "/search"
    private const val LOOKUP_PATH = "/lookup"
    private const val SONG_ENTITY = "song"
    private const val UNAVAILABLE_CODE = 503

    private val server = MockWebServer()
    private var isStarted = false

    @Volatile
    var isOffline = false

    val baseUrl: String
        get() {
            start()
            return server.url("/").toString()
        }

    /** Every path with its query that reached the server, in order. */
    val receivedCalls: List<String>
        get() = synchronized(calls) { calls.toList() }
    private val calls = mutableListOf<String>()

    @Synchronized
    fun start() {
        if (isStarted) return
        server.dispatcher = FixtureDispatcher()
        server.start()
        isStarted = true
    }

    fun reset() {
        isOffline = false
        synchronized(calls) { calls.clear() }
    }

    fun fixture(name: String): JSONObject = JSONObject(readAsset(name))

    private fun readAsset(name: String): String =
        InstrumentationRegistry.getInstrumentation().context.assets.open(name).bufferedReader().use { it.readText() }

    private class FixtureDispatcher : Dispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {
            synchronized(calls) { calls += request.url.encodedPath + "?" + request.url.encodedQuery }
            if (isOffline) {
                return MockResponse.Builder().code(UNAVAILABLE_CODE).build()
            }
            val body = when (request.url.encodedPath) {
                SEARCH_PATH -> searchBody(request.url.queryParameter("term").orEmpty())
                LOOKUP_PATH -> lookupBody(request)
                else -> return MockResponse.Builder().code(404).build()
            }
            // iTunes answers with `text/javascript`, which the parser must accept.
            return MockResponse.Builder().code(200).addHeader("Content-Type", "text/javascript; charset=utf-8").body(body).build()
        }

        private fun searchBody(term: String): String {
            val isNoResultsTerm = term.contains(NO_RESULTS_TERM)
            return readAsset(if (isNoResultsTerm) "empty.json" else "search_songs.json")
        }

        private fun lookupBody(request: RecordedRequest): String {
            val id = request.url.queryParameter("id").orEmpty()
            val wantsAlbumWithTracks = request.url.queryParameter("entity") == SONG_ENTITY
            if (wantsAlbumWithTracks) {
                return readAsset("lookup_album.json")
            }
            // A track is looked up by its id: it is found among the songs the fixtures hold, or made from the first one.
            val searchTracks = fixture("search_songs.json").results()
            val track = (searchTracks + fixture("lookup_album.json").results()).firstOrNull { it.optString("trackId") == id }
                ?: JSONObject(searchTracks.first().toString()).put("trackId", id.toLong()).put("trackName", "$SUGGESTED_NAME_PREFIX $id")
            return JSONObject().put("resultCount", 1).put("results", JSONArray().put(track)).toString()
        }

        private fun JSONObject.results(): List<JSONObject> = getJSONArray("results").let { array ->
            List(array.length()) { array.getJSONObject(it) }
        }
    }
}
