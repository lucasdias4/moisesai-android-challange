import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Pure JVM module: the compiler rejects Android imports, which is how :core:shared-domain-model
// stays free of them. Java 11 matches the Android modules that consume it.
plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    "testImplementation"(libs.findLibrary("junit").get())
}
