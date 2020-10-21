package com.judokit.android.examples.test

import android.os.Bundle
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.CucumberOptions
import java.io.File

@CucumberOptions(
    features = ["features"],
    glue = ["com.judokit.android.examples.test.steps"]
)
class CucumberRunner : CucumberAndroidJUnitRunner() {

    override fun onCreate(bundle: Bundle) {
        bundle.putString("plugin", getPluginConfigurationString())
        // we programmatically create the plugin configuration
        // it crashes on Android R without it
        File(getAbsoluteFilesPath()).mkdirs()
        super.onCreate(bundle)
    }

    /**
     * Since we want to checkout the external storage directory programmatically, we create the plugin configuration
     * here, instead of the [CucumberOptions] annotation.
     *
     * @return the plugin string for the configuration, which contains XML, HTML and JSON paths
     */
    private fun getPluginConfigurationString(): String? {
        val separator = "--"
        return "junit:${getCucumberXml()}${separator}html:${getCucumberHtml()}${separator}json:${getCucumberJson()}"
    }

    private fun getCucumberHtml(): String {
        return "${getAbsoluteFilesPath()}/cucumber.html"
    }

    private fun getCucumberXml(): String {
        return "${getAbsoluteFilesPath()}/cucumber.xml"
    }

    private fun getCucumberJson(): String {
        return "${getAbsoluteFilesPath()}/cucumber.json"
    }

    /**
     * The path which is used for the report files.
     *
     * @return the absolute path for the report files
     */
    private fun getAbsoluteFilesPath(): String {
        // sdcard/Android/data/com.judokit.android.examples
        val directory: File? = targetContext.getExternalFilesDir(null)
        return File(directory, "reports").absolutePath
    }
}
