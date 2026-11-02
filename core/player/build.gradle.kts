plugins {
    id("moisesai.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.moisesai.challange.core.player"
}

dependencies {
    implementation(libs.media3.exoplayer)
    implementation(libs.kotlinx.coroutines.android) // Dispatchers.Main: ExoPlayer is used from the main thread
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
