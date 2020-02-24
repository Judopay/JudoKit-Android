package com.judopay.model

enum class ApiEnvironment(val host: String) {
    LIVE("https://gw1.judopay.com/"),
    SANDBOX("https://gw1.judopay-sandbox.com/")
}
