import com.android.build.api.dsl.LibraryExtension
import com.judopay.judokit.configureAndroidLibraryAdditionalArtifacts
import com.judopay.judokit.configureMavenPublishing
import com.judopay.judokit.configureSigning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

class MavenPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
            }

            // append src and javadoc jars to the publication
            extensions.configure<LibraryExtension> {
                configureAndroidLibraryAdditionalArtifacts(this)
            }

            afterEvaluate {
                with(extensions) {
                    configure<PublishingExtension> {
                        configureMavenPublishing(this)
                    }
                    configure<SigningExtension> {
                        configureSigning(this)
                    }
                }
            }
        }
    }
}
