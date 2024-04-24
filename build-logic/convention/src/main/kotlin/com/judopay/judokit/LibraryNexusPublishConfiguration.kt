package com.judopay.judokit

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.api.Project

internal fun Project.configureNexusPublish(
    extension: NexusPublishExtension,
) = extension.apply {
    with(repositories.sonatype()) {
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
}
