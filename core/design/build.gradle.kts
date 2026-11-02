plugins {
    id("moisesai.android.library.compose")
}

android {
    namespace = "com.moisesai.challange.core.design"
}

dependencies {
    implementation(libs.haze) // backdrop blur of the glass surfaces
    implementation(libs.coil.compose) // reads the artwork colours that light the ambient background
}
