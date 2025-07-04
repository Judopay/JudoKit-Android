package com.judopay.judokit

import org.gradle.api.Project

object Versions {
    // Library versions, here you update the versions of the library
    const val VERSION_NAME: String = "5.0.1"

    // Android versions
    const val TARGET_SDK: Int = 35
    const val COMPILE_SDK: Int = 35
    const val MIN_SDK: Int = 21
}

private const val EXAMPLE_APP_VERSION_CODE = "android.injected.version.code"

fun Project.getVersionCode(): Int =
    if (hasProperty(EXAMPLE_APP_VERSION_CODE)) {
        property(EXAMPLE_APP_VERSION_CODE).toString().toInt()
    } else {
        1
    }
