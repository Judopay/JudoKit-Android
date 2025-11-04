package com.judopay.judokit

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project

internal fun Project.configureKover(
    extension: KoverProjectExtension,
) = extension.apply {

     reports {
         total {
             xml {
                 onCheck.set(true)
                 xmlFile.set(layout.buildDirectory.file("reports/kover/jacocoTestReport.xml"))
             }
         }
     }
}
