package com.judopay.judokit

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

internal fun Project.configureKotlin(commonExtension: CommonExtension) {
    commonExtension.compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

/**
 * Keeps the published library consumable by projects on an older Kotlin compiler than the
 * one we build with. A Kotlin compiler can only read metadata at most one minor version
 * newer than itself, and React Native (0.81 through 0.86) still builds with Kotlin 2.1.20.
 * Without this, consumers fail with "Module was compiled with an incompatible version of
 * Kotlin". Pinning the API/language version and the published kotlin-stdlib dependency
 * lets us upgrade the Kotlin Gradle plugin without forcing consumers to upgrade too.
 *
 * jvmDefault is pinned because language version 2.1 would otherwise fall back to the old
 * DefaultImpls-only output and drop interface default methods that earlier releases already
 * shipped.
 */
internal fun Project.configureKotlinConsumerCompatibility() {
    extensions.configure<KotlinAndroidProjectExtension> {
        coreLibrariesVersion = Versions.MIN_CONSUMER_KOTLIN
        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_1)
            languageVersion.set(KotlinVersion.KOTLIN_2_1)
            jvmDefault.set(JvmDefaultMode.ENABLE)
        }
    }
}
