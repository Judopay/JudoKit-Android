package com.judopay.judokit

import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBinaryCompatibilityValidator() {
    extensions.configure<ApiValidationExtension> {
        ignoredClasses.addAll(setOf(".*Binding$", "com.judopay.judokit.android.databinding.*"))
    }
}
