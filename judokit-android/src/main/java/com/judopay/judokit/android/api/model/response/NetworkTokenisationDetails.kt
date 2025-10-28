package com.judopay.judokit.android.api.model.response

class NetworkTokenisationDetails(
    val networkTokenProvisioned: Boolean? = null,
    val networkTokenUsed: Boolean? = null,
    val virtualPan: VirtualPan? = null,
) {
    override fun toString(): String {
        return """
            NetworkTokenisationDetails(
            networkTokenProvisioned=$networkTokenProvisioned,
            networkTokenUsed=$networkTokenUsed,
            virtualPan=$virtualPan)
        """
    }
}
