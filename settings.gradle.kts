pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Moisesai Challange"
include(":app")
include(":core:common")
include(":core:data-common")
include(":core:design")
include(":core:shared-domain-model")
include(":core:network")
include(":core:database")
include(":core:player")
include(":feature:songs")
include(":feature:songdetails")
include(":feature:album")
