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

    // ExoPlayer is real here: the tests play a tiny audio file from the assets, with no network.
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.junit)
}
