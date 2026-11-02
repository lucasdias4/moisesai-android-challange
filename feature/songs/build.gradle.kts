plugins {
    id("moisesai.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moisesai.challange.feature.songs"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(libs.kotlinx.serialization.json) // typed navigation route
    implementation(libs.coil.compose) // album artwork
    implementation(libs.coil.network.okhttp)
}
