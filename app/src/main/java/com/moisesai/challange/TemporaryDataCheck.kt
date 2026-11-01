package com.moisesai.challange

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.tracks
import com.moisesai.challange.core.network.model.ApiTrack
import kotlinx.coroutines.flow.first

private const val LOG_TAG = "DataCheck"
private const val SEARCH_TEXT = "jack johnson"
private const val MAX_RESULTS = 200
private const val PAGE_SIZE = 20
private const val RECENT_LIMIT = 10

//TODO: DELETE
/**
 * TEMPORARY: a manual check of the network and database layers that runs on every launch and shows
 * the outcome: it fetches and caches a search, reads the first page back from the database, and
 * records one more recent track each launch. Delete this file, its use in MainActivity and the
 * :core:network and :core:database dependencies in app/build.gradle.kts when the first real screen exists.
 */
@Composable
fun TemporaryDataCheck(
    trackRemoteDataSource: TrackRemoteDataSource,
    savedSearchLocalDataSource: SavedSearchLocalDataSource,
    recentlyPlayedTrackLocalDataSource: RecentlyPlayedTrackLocalDataSource,
    modifier: Modifier = Modifier,
) {
    var message by remember { mutableStateOf("Checking the network and the database...") }

    LaunchedEffect(Unit) {
        val network = fetchAndCache(trackRemoteDataSource, savedSearchLocalDataSource)
        val cache = readCachedPage(savedSearchLocalDataSource)
        val recents = recordRecentlyPlayedTrack(recentlyPlayedTrackLocalDataSource, cachedPage = cache.tracks)
        message = "Network: $network\nCache, first page: ${cache.summary}\nRecent tracks: $recents"
        Log.d(LOG_TAG, message)
    }

    Text(text = message, modifier = modifier)
}

private suspend fun fetchAndCache(
    trackRemoteDataSource: TrackRemoteDataSource,
    savedSearchLocalDataSource: SavedSearchLocalDataSource,
): String = trackRemoteDataSource.searchTracksBySearchText(SEARCH_TEXT, MAX_RESULTS).fold(
    onSuccess = { response ->
        val tracks = response.tracks().map { it.toDb() }
        val search = DbSavedSearch(SEARCH_TEXT, fetchedAtMillis = System.currentTimeMillis())
        val saved = savedSearchLocalDataSource.saveSearch(search, tracks)
        "${tracks.size} tracks fetched, ${if (saved.isSuccess) "saved" else "NOT saved: ${saved.exceptionOrNull()?.message}"}"
    },
    onFailure = { "failed (${it.message})" },
)

private class CachedPage(val tracks: List<DbTrack>, val summary: String)

private suspend fun readCachedPage(savedSearchLocalDataSource: SavedSearchLocalDataSource): CachedPage =
    savedSearchLocalDataSource.getSavedSearchTracksPage(SEARCH_TEXT, PAGE_SIZE, offset = 0).fold(
        onSuccess = { CachedPage(it, "${it.size} tracks, first: ${it.firstOrNull()?.name}") },
        onFailure = { CachedPage(emptyList(), "failed (${it.message})") },
    )

private suspend fun recordRecentlyPlayedTrack(
    recentlyPlayedTrackLocalDataSource: RecentlyPlayedTrackLocalDataSource,
    cachedPage: List<DbTrack>,
): String {
    val before = recentlyPlayedTrackLocalDataSource.observeRecentlyPlayedTracks(RECENT_LIMIT).first().getOrNull().orEmpty()
    cachedPage.getOrNull(before.size)?.let { track ->
        recentlyPlayedTrackLocalDataSource.saveRecentlyPlayedTrack(track, playedAtMillis = System.currentTimeMillis())
    }
    val after = recentlyPlayedTrackLocalDataSource.observeRecentlyPlayedTracks(RECENT_LIMIT).first().getOrNull().orEmpty()
    return "${before.size} before, ${after.size} now, newest: ${after.firstOrNull()?.name}"
}

private fun ApiTrack.toDb() = DbTrack(
    id = id,
    name = name,
    artistName = artistName,
    albumId = albumId,
    albumName = albumName,
    artUrl = artUrl,
    previewUrl = previewUrl,
    trackTimeMillis = trackTimeMillis,
    releaseDate = releaseDate,
    trackNumber = trackNumber,
)
