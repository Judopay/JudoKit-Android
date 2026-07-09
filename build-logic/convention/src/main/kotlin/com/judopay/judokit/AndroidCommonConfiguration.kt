package com.judopay.judokit

import com.android.build.api.dsl.CommonExtension

internal fun configureAndroidCommon(extension: CommonExtension) =
    extension.apply {
        compileSdk = Versions.COMPILE_SDK

        defaultConfig.apply {
            minSdk = Versions.MIN_SDK
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        packaging.apply {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildFeatures.apply {
            buildConfig = true
        }

        testOptions.apply {
            animationsDisabled = true

            unitTests {
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
        }
    }
