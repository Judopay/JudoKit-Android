import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.judopay.judokit.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidKotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
            }

            when {
                pluginManager.hasPlugin("com.android.library") -> {
                    configure<LibraryExtension> {
                        configureKotlin(this)
                    }
                }
                pluginManager.hasPlugin("com.android.application") -> {
                    configure<ApplicationExtension> {
                        configureKotlin(this)
                    }
                }
                else -> {
                    logger.warn("AndroidKotlinConventionPlugin applied to project without Android plugin.")
                }
            }
        }
    }
}
