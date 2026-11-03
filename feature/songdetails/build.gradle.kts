plugins {
    id("moisesai.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.moisesai.challange.feature.songdetails"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:player"))
    implementation(libs.kotlinx.serialization.json) // typed navigation route
    implementation(libs.coil.compose) // song artwork
}
