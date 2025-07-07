package com.judopay.judokit

import com.android.build.api.dsl.LibraryExtension

internal fun configureAndroidLibrary(extension: LibraryExtension) =
    extension.apply {
        buildFeatures {
            viewBinding = true
        }

        defaultConfig {
            consumerProguardFiles("consumer-rules.pro")
        }
    }
