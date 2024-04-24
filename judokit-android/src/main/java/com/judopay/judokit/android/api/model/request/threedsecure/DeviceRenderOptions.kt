package com.judopay.judokit.android.api.model.request.threedsecure

data class DeviceRenderOptions(
    private val sdkInterface: String = "BOTH",
    private val sdkUiType: List<SdkUiType> = SdkUiType.values().toList(),
)
