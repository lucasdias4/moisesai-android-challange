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
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.tracks

private const val LOG_TAG = "NetworkCheck"
private const val SEARCH_TEXT = "jack johnson"
private const val MAX_RESULTS = 200

/**
 * TEMPORARY: a manual check of the network layer that runs on every launch and shows the outcome.
 * Delete this file, its use in MainActivity and the :core:network dependency in app/build.gradle.kts
 * when the first real screen exists.
 */
@Composable
fun TemporaryNetworkCheck(trackRemoteDataSource: TrackRemoteDataSource, modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("Searching iTunes for \"$SEARCH_TEXT\"...") }

    LaunchedEffect(Unit) {
        message = trackRemoteDataSource.searchTracksBySearchText(SEARCH_TEXT, MAX_RESULTS).fold(
            onSuccess = { response ->
                val tracks = response.tracks()
                "${tracks.size} tracks found.\nFirst: ${tracks.firstOrNull()?.name}"
            },
            onFailure = { error -> "Request failed: ${error.message}" },
        )
        Log.d(LOG_TAG, message)
    }

    Text(text = message, modifier = modifier)
}
