package com.judokit.android.test

import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.CucumberOptions

@CucumberOptions(features = ["features"], glue = ["com.judokit.android.test.steps"])
class CucumberRunner: CucumberAndroidJUnitRunner()