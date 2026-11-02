package com.moisesai.challange

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.navigation.AppNavHost
import com.moisesai.challange.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The Aurora Neon theme is dark only, so the system bar icons are always light.
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
                    AppNavHost()
                }
            }
        }
    }
}
