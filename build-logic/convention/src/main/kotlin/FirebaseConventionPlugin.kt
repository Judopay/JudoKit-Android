import com.android.build.api.dsl.ApplicationExtension
import com.judopay.judokit.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class FirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                add("implementation", platform(versionCatalog.findLibrary("firebase.bom").get()))
                add("implementation", versionCatalog.findLibrary("firebase.analytics").get())
                add("implementation", versionCatalog.findLibrary("firebase.crashlytics").get())
            }

            disableCrashlyticsInDebugAppBuilds()
        }
    }
}

private const val COLLECTION_ENABLED_KEY = "crashlyticsCollectionEnabled"

private fun Project.disableCrashlyticsInDebugAppBuilds() {
    if (pluginManager.hasPlugin("com.android.application")) {
        configure<ApplicationExtension> {
            buildTypes {
                release {
                    manifestPlaceholders[COLLECTION_ENABLED_KEY] = "true"
                }
                debug {
                    manifestPlaceholders[COLLECTION_ENABLED_KEY] = "false"
                }
            }
        }
    }
}
