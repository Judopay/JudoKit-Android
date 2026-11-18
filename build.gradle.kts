import com.judopay.judokit.Namespaces
import com.judopay.judokit.Versions

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.devtools.ksp) apply false

    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.binary.compatibility.validator) apply false
    alias(libs.plugins.junit5) apply false
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.kover) apply false
    id("judokit.nexus-publish")

    // sample app crash reporting
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

allprojects {
    group = Namespaces.GROUP
    version = Versions.VERSION_NAME
}

subprojects {
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "io.netty") {
                useVersion(libs.versions.netty.get())
                because(
                    "Pin Netty versions used by AGP Unified Test Platform to address critical netty-handler findings " +
                        "(Snyk: com.android.tools.utp:android-test-plugin-result-listener-gradle → " +
                        "com.google.testing.platform:core:0.0.9-alpha04 → io.netty:netty-handler:4.1.93.Final)",
                )
            }
        }
    }
}
