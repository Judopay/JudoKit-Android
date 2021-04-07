package com.judopay.judokit.android.model

enum class ApiEnvironment(val host: String) {
    LIVE("https://api.judopay.com/"),
    SANDBOX("https://gw1.karatepay-sandbox.com/")
}
