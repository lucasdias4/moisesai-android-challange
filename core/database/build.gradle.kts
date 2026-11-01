plugins {
    id("moisesai.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room3)
}

android {
    namespace = "com.moisesai.challange.core.database"
}

// Room writes the schema here on every build. It is git-ignored: once the database has a released version,
// keep that version's file so a migration can be validated against it.
room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // api: the local datasources return Result<..AppError..>, so consumers need the type.
    api(project(":core:common"))
    implementation(project(":core:data-common"))

    implementation(libs.room3.runtime)
    implementation(libs.androidx.sqlite.framework)
    ksp(libs.room3.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room runs on the device's real SQLite, so DAO and datasource tests are instrumented tests.
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
}
