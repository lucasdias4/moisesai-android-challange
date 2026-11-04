plugins {
    id("moisesai.android.library.compose")
}

android {
    namespace = "com.moisesai.challange.core.design"
}

dependencies {
    implementation(project(":core:common")) // AppError, to tell a network failure from any other
    implementation(project(":core:strings")) // the description of the back button
}
