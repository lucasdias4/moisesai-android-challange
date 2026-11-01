package com.moisesai.challange

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.database.datasource.RecentlyPlayedTrackLocalDataSource
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.splash.SplashScreen
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
        // The Aurora Glass theme is dark only, so the system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            MoisesaiChallangeTheme {
                // TODO: Replace with the splash destination of the NavHost.
                var isSplashVisible by rememberSaveable { mutableStateOf(true) }
                if (isSplashVisible) {
                    SplashScreen(onFinished = { isSplashVisible = false })
                } else {
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
}
