import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.judopay.judokit.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidKotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // AGP 9 with builtInKotlin=true applies kotlin-android automatically; applying
            // it again would register the 'kotlin' extension twice. kotlin-parcelize is
            // applied here so it is registered before KGP finalises its compiler-plugin list.
            pluginManager.apply("kotlin-parcelize")

            pluginManager.withPlugin("org.jetbrains.kotlin.android") {
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
}
