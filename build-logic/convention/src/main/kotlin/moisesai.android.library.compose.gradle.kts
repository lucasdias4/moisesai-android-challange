plugins {
    id("moisesai.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    "implementation"(platform(libs.library("androidx-compose-bom")))
    "implementation"(libs.library("androidx-compose-material3"))
    "implementation"(libs.library("androidx-compose-ui"))
    "implementation"(libs.library("androidx-compose-ui-graphics"))
    "implementation"(libs.library("androidx-compose-ui-tooling-preview"))
    "debugImplementation"(libs.library("androidx-compose-ui-tooling"))
}
