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

dependencies {
    "testImplementation"(libs.library("junit"))
    "testImplementation"(libs.library("mockk"))
    "testImplementation"(libs.library("kotlinx-coroutines-test"))
    "testImplementation"(libs.library("turbine"))
}
