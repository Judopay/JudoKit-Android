package com.judopay.model

enum class ApiEnvironment(val host: String) {
    LIVE("https://api.judopay.com/"),
    SANDBOX("https://api-sandbox.judopay.com/")
}
