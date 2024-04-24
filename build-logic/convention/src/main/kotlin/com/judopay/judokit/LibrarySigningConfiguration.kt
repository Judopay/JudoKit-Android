package com.judopay.judokit

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.SigningExtension

internal fun Project.configureSigning(
    extension: SigningExtension
) = extension.apply {
    val signingKey = findProperty("signingKey")?.toString()
    val signingKeyId = findProperty("signingKeyId")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()

    if (signingKey.isNullOrBlank() || signingPassword.isNullOrBlank()) {
        logger.warn("No signing key or password provided, skipping signing configuration.")
        return@apply
    }

    if (signingKeyId.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    } else {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }

    val publications = extensions.getByType<PublishingExtension>().publications

    sign(publications)
}
