import org.gradle.api.Plugin
import org.gradle.api.Project
import com.judopay.judokit.configureKover
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.kotlin.dsl.configure

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlinx.kover")
            }

            extensions.configure<KoverProjectExtension> {
                configureKover(this)
            }
        }
    }
}
