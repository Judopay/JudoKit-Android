package com.judokit.android.examples.test

import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.CucumberOptions

@CucumberOptions(features = ["features"], glue = ["com.judokit.android.examples.test.steps"])
class CucumberRunner: CucumberAndroidJUnitRunner()