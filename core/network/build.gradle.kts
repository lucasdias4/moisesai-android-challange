plugins {
    id("moisesai.android.library")
}

android {
    namespace = "com.moisesai.challange.core.network"
}

dependencies {
    testImplementation(libs.okhttp.mockwebserver)
}
