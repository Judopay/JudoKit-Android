package com.judopay.judokit

import com.android.build.api.dsl.CommonExtension

@Suppress("UnstableApiUsage")
internal fun configureAndroidCommon(
    extension: CommonExtension<*, *, *, *, *, *>,
) = extension.apply {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.MIN_SDK
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        buildConfig = true // required for buildConfigField calls
    }

    testOptions {

        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
