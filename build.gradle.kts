// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room3) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kover)
}

// One place for the code style: every module is checked with the same ktlint version and the rules of .editorconfig.
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(rootProject.libs.versions.ktlint.get())
        android.set(true)
    }
}

// Unit-test coverage (Kover, JVM tests only): the report of every module is merged into one, `./gradlew koverHtmlReport`
// (build/reports/kover/html) or `./gradlew koverLog` for the number. The emulator tests (functional and end to end) are not
// measured: Kover does not read them and JaCoCo is not set up.
dependencies {
    kover(project(":app"))
    kover(project(":core:common"))
    kover(project(":core:data-common"))
    kover(project(":core:design"))
    kover(project(":core:shared-domain-model"))
    kover(project(":core:network"))
    kover(project(":core:database"))
    kover(project(":core:player"))
    kover(project(":feature:songs"))
    kover(project(":feature:songdetails"))
    kover(project(":feature:album"))
}

// What a unit test is not meant to cover: the screens and components (functional tests on the emulator), the dependency
// injection wiring and the code that Hilt, Room and Android generate.
kover {
    reports {
        filters {
            excludes {
                annotatedBy("androidx.compose.runtime.Composable", "androidx.compose.ui.tooling.preview.Preview")
                classes(
                    "*ComposableSingletons*",
                    "*_Factory",
                    "*_Factory\$*",
                    "*_HiltModules*",
                    "*Hilt_*",
                    "dagger.hilt.*",
                    "hilt_aggregated_deps.*",
                    "*_Impl",
                    "*_Impl\$*",
                    "*.BuildConfig",
                    "*.R",
                    "*.R\$*",
                )
                packages("*.di", "*.composable", "*.animation")
            }
        }
    }
}
