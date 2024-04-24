@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Ravelin - optional - only required if you are using Ravelin
        maven("https://maven.ravelin.com/public/repositories/core-android/")
    }
}

rootProject.name="JudoKit-Android"
include(":judokit-android-examples", ":judokit-android")
