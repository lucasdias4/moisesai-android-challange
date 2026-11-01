// Single place for what every :feature:* module shares. Feature modules never depend on each
// other; only the shared :core:* modules are listed here. Data-owning features add
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
    "implementation"(libs.library("hilt-android"))
    "ksp"(libs.library("hilt-compiler"))
}
