plugins {
    id("moisesai.android.feature")
}

android {
    namespace = "com.moisesai.challange.feature.songs"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
}
