import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.judopay.judokit.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.nexus.publish.gradle.plugin)
    compileOnly(libs.kotlin.binary.compatibility.validator)

    compileOnly(libs.bundles.dokka)
    runtimeOnly(libs.bundles.dokka)

    compileOnly(libs.kover.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "judokit.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "judokit.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidKotlin") {
            id = "judokit.android.kotlin"
            implementationClass = "AndroidKotlinConventionPlugin"
        }
        register("kover") {
            id = "judo3ds2.kotlinx.kover"
            implementationClass = "KoverConventionPlugin"
        }
        register("mavenPublish") {
            id = "judokit.maven-publish"
            implementationClass = "MavenPublishConventionPlugin"
        }
        register("nexusPublish") {
            id = "judokit.nexus-publish"
            implementationClass = "NexusPublishConventionPlugin"
        }
        register("androidLint") {
            id = "judokit.android.lint"
            implementationClass = "AndroidLintConventionPlugin"
        }
        register("firebase") {
            id = "judokit.firebase"
            implementationClass = "FirebaseConventionPlugin"
        }
    }
}
