plugins {
    id("moisesai.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moisesai.challange.core.network"
}

dependencies {
    // api: the remote datasource returns Result<..AppError..>, so consumers need the type.
    api(project(":core:common"))

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.okhttp.mockwebserver)
}
