plugins {
    `kotlin-dsl`
}

// The precompiled scripts apply these plugins by id, so they must be on this build's classpath.
dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.compose.gradlePlugin)
}
