package com.moisesai.challange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var trackRemoteDataSource: TrackRemoteDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoisesaiChallangeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TemporaryNetworkCheck(
                        trackRemoteDataSource = trackRemoteDataSource,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
