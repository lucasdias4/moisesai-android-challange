plugins {
    id("com.android.library")
}

android {
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Hilt generates androidTest sources, so without this every library gets an instrumented
// test APK even when it has no tests, and that APK crashes (no test runner on its classpath).
androidComponents {
    beforeVariants { variant ->
        variant.androidTest.enable = file("src/androidTest").exists()
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    "testImplementation"(libs.findLibrary("junit").get())
    "testImplementation"(libs.findLibrary("mockk").get())
    "testImplementation"(libs.findLibrary("kotlinx-coroutines-test").get())
    "testImplementation"(libs.findLibrary("turbine").get())
}
