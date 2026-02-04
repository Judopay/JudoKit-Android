package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class NetworkTokenisationDetails(
    val networkTokenProvisioned: Boolean? = null,
    val networkTokenUsed: Boolean? = null,
    val virtualPan: VirtualPan? = null,
    val accountDetailsUpdated: Boolean? = null,
) : Parcelable {
    override fun toString(): String {
        return """
            NetworkTokenisationDetails(
            networkTokenProvisioned=$networkTokenProvisioned,
            networkTokenUsed=$networkTokenUsed,
            virtualPan=$virtualPan,
            accountDetailsUpdated=$accountDetailsUpdated)
        """
    }
}
