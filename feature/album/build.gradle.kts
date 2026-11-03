plugins {
    id("moisesai.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moisesai.challange.feature.album"
}

dependencies {
    implementation(project(":core:network"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
}
