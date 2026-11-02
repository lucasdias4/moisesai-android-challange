// Single place for what every :feature:* module shares. Feature modules never depend on each
// other; only the shared :core:* modules and the libraries every screen uses are listed here. Data-owning features add
// :core:network, :core:database or :core:player in their own build file.
plugins {
    id("moisesai.android.library.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

dependencies {
    "implementation"(project(":core:design"))
    "implementation"(project(":core:common"))
    "implementation"(project(":core:shared-domain-model"))
    "implementation"(project(":core:strings"))
    "implementation"(libs.library("hilt-android"))
    "ksp"(libs.library("hilt-compiler"))

    // What a screen needs: a ViewModel from Hilt, state collected with the lifecycle, and navigation.
    "implementation"(libs.library("androidx-lifecycle-viewmodel-compose"))
    "implementation"(libs.library("androidx-lifecycle-runtime-compose"))
    "implementation"(libs.library("androidx-hilt-navigation-compose"))
    "implementation"(libs.library("androidx-navigation-compose"))

    // Compose UI tests of the screens.
    "androidTestImplementation"(platform(libs.library("androidx-compose-bom")))
    "androidTestImplementation"(libs.library("androidx-compose-ui-test-junit4"))
    "androidTestImplementation"(libs.library("androidx-junit"))
    // Compose tests run through Espresso; an old version crashes on the newest Android versions.
    "androidTestImplementation"(libs.library("androidx-espresso-core"))
    "debugImplementation"(libs.library("androidx-compose-ui-test-manifest"))
}
