package com.judopay.judokit

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import kotlinx.validation.ApiValidationExtension
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Project
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import javax.inject.Inject

/**
 * Adapter around BCV's [KotlinApiBuildTask] so it can be fed through AGP's
 * ScopedArtifacts API, which hands over classes as separate jar/directory lists.
 * Jars are expanded with [ArchiveOperations.zipTree] because the BCV worker only
 * picks up `.class` files from [KotlinApiBuildTask.getInputClassesDirs].
 */
abstract class BuiltInKotlinApiBuildTask
    @Inject
    constructor(
        archiveOperations: ArchiveOperations,
    ) : KotlinApiBuildTask() {
        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        abstract val classesJars: ListProperty<RegularFile>

        @get:InputFiles
        @get:PathSensitive(PathSensitivity.RELATIVE)
        abstract val classesDirs: ListProperty<Directory>

        init {
            inputClassesDirs.from(classesDirs)
            inputClassesDirs.from(classesJars.map { jars -> jars.map(archiveOperations::zipTree) })
        }
    }

/**
 * BCV 0.18 registers its tasks only in reaction to a Kotlin Gradle plugin
 * (`kotlin`, `kotlin-android` or `kotlin-multiplatform`). With AGP 9's built-in
 * Kotlin none of those plugins is applied, so BCV applies cleanly but creates no
 * tasks and `check` silently stops validating the public API. Until BCV supports
 * built-in Kotlin, apiBuild/apiCheck/apiDump are registered here manually, fed
 * with the release variant classes. The BCV plugin is still applied so that
 * [ApiValidationExtension] exists — [KotlinApiBuildTask] reads its filters from
 * it as convention defaults. If a future BCV version starts registering these
 * tasks itself, this will fail with a duplicate-task error: delete this wiring.
 *
 * The same applies to the worker classpath (`bcv-rt-jvm-cp`): without a Kotlin
 * plugin BCV neither adds `kotlin-metadata-jvm` to it nor wires it into
 * [KotlinApiBuildTask.runtimeClasspath], so both are done manually below.
 */
internal fun Project.configureBinaryCompatibilityValidator() {
    extensions.configure<ApiValidationExtension> {
        ignoredClasses.addAll(setOf(".*Binding$", "com.judopay.judokit.android.databinding.*"))
    }

    // With built-in Kotlin, BCV never adds this itself; match the compiler version.
    dependencies.add(
        "bcv-rt-jvm-cp",
        "org.jetbrains.kotlin:kotlin-metadata-jvm:${versionCatalog.requiredVersion("kotlin")}",
    )

    val projectName = name
    val apiFileName = "$projectName.api"
    val apiDir = layout.projectDirectory.dir("api")

    val apiBuild =
        tasks.register<BuiltInKotlinApiBuildTask>("apiBuild") {
            description = "Builds Kotlin API for 'release' compilation of :$projectName"
            outputApiFile.set(layout.buildDirectory.file("api/$apiFileName"))
            // BCV wires the worker classpath only into tasks it registers itself.
            runtimeClasspath.from(configurations.named("bcv-rt-jvm-cp-resolver"))
        }

    extensions.configure<LibraryAndroidComponentsExtension> {
        onVariants(selector().withName("release")) { variant ->
            variant.artifacts
                .forScope(ScopedArtifacts.Scope.PROJECT)
                .use(apiBuild)
                .toGet(
                    ScopedArtifact.CLASSES,
                    BuiltInKotlinApiBuildTask::classesJars,
                    BuiltInKotlinApiBuildTask::classesDirs,
                )
        }
    }

    val apiCheck =
        tasks.register<KotlinApiCompareTask>("apiCheck") {
            group = "verification"
            description = "Checks signatures of public API against the golden value in the API folder for :$name"
            projectApiFile.set(apiDir.file(apiFileName))
            generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
        }

    tasks.register<Sync>("apiDump") {
        description = "Syncs the API file for :$name with the generated one"
        from(apiBuild.flatMap { it.outputApiFile })
        into(apiDir)
    }

    tasks.named("check") { dependsOn(apiCheck) }
}