import com.judopay.judokit.configureNexusPublish
import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class NexusPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<NexusPublishExtension> {
                configureNexusPublish(this)
            }
        }
    }
}
