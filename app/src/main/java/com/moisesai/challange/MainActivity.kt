package com.moisesai.challange

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.moisesai.challange.core.design.theme.MoisesaiChallangeTheme
import com.moisesai.challange.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Before super: it swaps the starting theme (system splash) for the app theme.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // The design is dark only, so the system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            MoisesaiChallangeTheme {
                AppNavHost()
            }
        }
    }
}
