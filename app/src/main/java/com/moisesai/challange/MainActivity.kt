package com.moisesai.challange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // TODO: Remove the temporary reference to DATA from the UI.
    @Inject
    lateinit var trackRemoteDataSource: TrackRemoteDataSource

    // TODO: Remove the temporary reference to DATA from the UI.
    @Inject
    lateinit var savedSearchLocalDataSource: SavedSearchLocalDataSource

    // TODO: Remove the temporary reference to DATA from the UI.
    @Inject
    lateinit var recentlyPlayedTrackLocalDataSource: RecentlyPlayedTrackLocalDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoisesaiChallangeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TemporaryDataCheck(
                        trackRemoteDataSource = trackRemoteDataSource,
                        savedSearchLocalDataSource = savedSearchLocalDataSource,
                        recentlyPlayedTrackLocalDataSource = recentlyPlayedTrackLocalDataSource,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
