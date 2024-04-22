package com.judopay.judokit

import com.android.build.api.dsl.LibraryExtension

internal fun configureAndroidLibraryAdditionalArtifacts(
    extension: LibraryExtension,
) = extension.apply {
    publishing {
        multipleVariants {
            withJavadocJar()
            withSourcesJar()
            allVariants()
        }
    }
}
