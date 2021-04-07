package com.judopay.judokit.android.api.model.request.threedsecure

data class DeviceRenderOptions(
    private val sdkInterface: String = "BOTH",
    private val sdkUiType: List<String> = listOf(
        "TEXT",
        "SINGLE_SELECT",
        "MULTI_SELECT",
        "OOB",
        "HTML_OTHER"
    )
)
