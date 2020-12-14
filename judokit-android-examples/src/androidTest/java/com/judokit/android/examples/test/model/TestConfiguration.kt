package com.judokit.android.examples.test.model

data class TestConfiguration(
    val judoId: String,
    val token: String,
    val secret: String,
    val threeDSToken: String,
    val threeDSSecret: String,
    val sandbox: Boolean,
    val testsToSkip: List<String>,
    val testData: List<TestData>,
    val defaultCards: List<Card>
)
