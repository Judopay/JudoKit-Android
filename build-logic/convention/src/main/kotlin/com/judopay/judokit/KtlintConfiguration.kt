package com.judopay.judokit

import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

internal fun configureKtlint(extension: KtlintExtension) =
    extension.apply {
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")

        reporters {
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
        }

        filter {
            exclude {
                // Generated DAO files
                it.file.path.contains("/_Impl.kt/")
            }
        }
    }
