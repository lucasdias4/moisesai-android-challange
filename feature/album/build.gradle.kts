plugins {
    id("moisesai.android.feature")
}

android {
    namespace = "com.moisesai.challange.feature.album"
}

dependencies {
    implementation(project(":core:network"))
}
