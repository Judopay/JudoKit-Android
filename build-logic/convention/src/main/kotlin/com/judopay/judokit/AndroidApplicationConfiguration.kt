package com.judopay.judokit

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

internal fun Project.configureAndroidApplication(extension: ApplicationExtension) =
    extension.apply {
        val appIdSuffix = findProperty("applicationIdSuffix")?.toString()

        defaultConfig {
            targetSdk = Versions.TARGET_SDK
            versionCode = getVersionCode()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            addBuildConfigFields()
        }

        buildFeatures {
            compose = true
            viewBinding = true
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
                applicationIdSuffix = appIdSuffix.takeIf { !it.isNullOrBlank() }
            }
        }
    }

private fun ApplicationDefaultConfig.addBuildConfigFields() {
    buildConfigField("String", "JUDO_ID", "\"${System.getenv("JUDO_ID")}\"")
    buildConfigField("String", "API_TEST_TOKEN", "\"${System.getenv("API_TEST_TOKEN")}\"")
    buildConfigField("String", "API_TEST_SECRET", "\"${System.getenv("API_TEST_SECRET")}\"")
    buildConfigField("String", "RECOMMENDATION_URL", "\"${System.getenv("RECOMMENDATION_URL")}\"")
    buildConfigField("String", "RSA_KEY", "\"${System.getenv("RSA_KEY")}\"")
}
