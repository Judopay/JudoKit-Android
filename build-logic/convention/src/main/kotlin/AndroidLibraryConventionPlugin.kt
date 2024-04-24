import com.android.build.gradle.LibraryExtension
import com.judopay.judokit.configureAndroidCommon
import com.judopay.judokit.configureAndroidLibrary
import com.judopay.judokit.configureBinaryCompatibilityValidator
import com.judopay.judokit.configureDokka
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.dokka")
                apply("com.google.devtools.ksp")
                apply("de.mannodermaus.android-junit5")
                apply("org.jetbrains.kotlinx.binary-compatibility-validator")
            }

            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)
                configureAndroidLibrary(this)
            }

            configureDokka()
            configureBinaryCompatibilityValidator()
        }
    }
}
