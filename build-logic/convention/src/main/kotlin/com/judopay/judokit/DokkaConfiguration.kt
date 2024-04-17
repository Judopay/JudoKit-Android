package com.judopay.judokit

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering

import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import java.time.Year

internal fun Project.configureDokka() {
    tasks.withType<DokkaTask>().configureEach {
        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "(c) Judopay ${Year.now().value}. All rights reserved."
           // todo: configure other Dokka settings
        }
    }
}

internal fun Project.javadoc(): TaskProvider<Jar> {
    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")

        val dokkaJavadocTask = tasks.getByName("dokkaJavadoc")

        from(dokkaJavadocTask)
        dependsOn(dokkaJavadocTask)
    }
    return javadocJar
}
