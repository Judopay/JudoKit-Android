package com.judopay.model

enum class ApiEnvironment(val host: String) {
    LIVE("https://api.karatepay.com/"),
    SANDBOX("https://api-sandbox.karatepay.com/")
}
