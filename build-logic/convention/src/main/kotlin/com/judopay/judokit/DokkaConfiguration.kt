package com.judopay.judokit

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import java.time.Year

@OptIn(InternalDokkaGradlePluginApi::class)
internal fun Project.configureDokka() {
    extensions.configure<DokkaExtension> {
        pluginsConfiguration.withType<DokkaHtmlPluginParameters>().configureEach {
            footerMessage.set("(c) Judopay ${Year.now().value}. All rights reserved.")
        }
    }
}
