package com.judopay.judokit

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.api.Project

internal fun Project.configureNexusPublish(
    extension: NexusPublishExtension,
) = extension.apply {
    with(repositories.sonatype()) {
        nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
        snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
    }
}
