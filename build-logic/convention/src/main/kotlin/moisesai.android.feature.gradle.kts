// Single place for what every :feature:* module shares. Feature modules never depend on each
// other; only the shared :core:* modules are listed here. Data-owning features add
// :core:network, :core:database or a :library:* module in their own build file.
plugins {
    id("moisesai.android.library.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    "implementation"(project(":core:design"))
    "implementation"(project(":core:common"))
    "implementation"(project(":core:shared-domain-model"))
    "implementation"(libs.findLibrary("hilt-android").get())
    "ksp"(libs.findLibrary("hilt-compiler").get())
}
