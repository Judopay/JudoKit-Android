package com.judopay.judokit

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

internal fun Project.configureMavenPublishing(
    extension: PublishingExtension
) = extension.apply {
    val javadoc = javadoc()

    publications {
        create<MavenPublication>("judokit-android-release") {
            artifactId = "judokit-android"
            groupId = "com.judopay"
            version = Versions.VERSION_NAME

            from(components["release"])
            artifact(javadoc.get())

            configurePom()
        }
    }
}

private fun MavenPublication.configurePom() = pom {
    name.set("Judopay JudoKit-Android")
    description.set("JudoKit-Android is an SDK to assist Android app developers to accept payments.")
    url.set("https://github.com/Judopay/JudoKit-Android")

    developers {
        developer {
            id.set("judopay")
            name.set("Judopay")
            email.set("developersupport@judopay.com")
            organization.set("Judopay")
            organizationUrl.set("https://www.judopay.com")
        }
    }

    licenses {
        license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
        }
    }

    scm {
        url.set("https://github.com/Judopay/JudoKit-Android")
        connection.set("scm:git:git://github.com/Judopay/JudoKit-Android.git")
        developerConnection.set("scm:git:ssh://github.com:Judopay/JudoKit-Android.git")
    }
}
