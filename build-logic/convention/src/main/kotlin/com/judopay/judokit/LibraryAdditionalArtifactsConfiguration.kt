package com.judopay.judokit

import com.android.build.api.dsl.LibraryExtension

internal fun configureAndroidLibraryAdditionalArtifacts(
    extension: LibraryExtension,
) = extension.apply {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
        multipleVariants {
            allVariants()
            withJavadocJar()
            withSourcesJar()
        }
    }
}
