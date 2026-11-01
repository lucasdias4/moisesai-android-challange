plugins {
    id("moisesai.android.feature")
}

android {
    namespace = "com.moisesai.challange.feature.songdetails"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:player"))
}
