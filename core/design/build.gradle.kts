plugins {
    id("moisesai.android.library.compose")
}

android {
    namespace = "com.moisesai.challange.core.design"
}

dependencies {
    implementation(project(":core:strings")) // the description of the back button
    implementation(libs.haze) // blurs what is behind a surface (the action sheet)
}
