plugins {
    id("moisesai.android.library.compose")
}

android {
    namespace = "com.moisesai.challange.core.design"
}

dependencies {
    implementation(project(":core:common")) // AppError, to tell a network failure from any other
    implementation(project(":core:strings")) // the description of the back button
    implementation(libs.coil.compose) // CoverImageComponent: the cover picture with its shimmer and fade

    // Compose UI tests of the components and the shared animations.
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    // Compose tests run through Espresso; an old version crashes on the newest Android versions.
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
