import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.judopay.judokit.configureKtlint
import com.judopay.judokit.library
import com.judopay.judokit.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jlleitschuh.gradle.ktlint")
                apply("io.gitlab.arturbosch.detekt")
            }

            extensions.configure<KtlintExtension> {
                configureKtlint(this)
            }

            when {
                pluginManager.hasPlugin("com.android.library") -> {
                    configure<LibraryExtension> {
                        lint(Lint::configure)
                    }
                }
                pluginManager.hasPlugin("com.android.application") -> {
                    dependencies {
                        add("ktlintRuleset", versionCatalog.library("ktling-compose-rules"))
                    }
                    configure<ApplicationExtension> {
                        lint(Lint::configure)
                    }
                }
                else -> {
                    logger.warn("AndroidLintConventionPlugin applied to project without Android plugin.")
                }
            }
        }
    }
}


private fun Lint.configure() {
    xmlReport = true
    checkDependencies = true
}
