import com.judopay.judokit.Namespaces
import com.judopay.judokit.Versions

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false

    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.binary.compatibility.validator) apply false
    alias(libs.plugins.junit5) apply false
    alias(libs.plugins.nexus.publish)
    id("judokit.nexus-publish")

    // sample app crash reporting
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

allprojects {
    group = Namespaces.GROUP
    version = Versions.VERSION_NAME
}
