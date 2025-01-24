package com.judopay.judokit.android

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.ui.cardentry.NfcCardManagerImproved
import com.vignesh.nfccardreader.NfcCardReader
import com.vignesh.nfccardreader.model.EmvCard

class NfcService(
    private val nfcCardManager: NfcCardManagerImproved
) {
    private val nfcCardReader = NfcCardReader()
    var onResult: ((CardScanningResult) -> Unit)? = null

    val demoVideoMode = true
    val fakeCardNumber = "4929 1512 0321 1009"
    val fakeCardExpDate = "04/30"

    fun enableNfcScanning(onResult: ((CardScanningResult) -> Unit)) {
        nfcCardManager.enableDispatch()
        this.onResult = onResult
    }

    fun disableNfcScanning() {
        nfcCardManager.disableDispatch()
    }

    fun handleNfcIntent(intent: Intent): CardScanningResult? {
        intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let { tag ->
            nfcCardReader.readCard(tag)?.emvCard?.let { emvCard ->
                onResult?.let {
                    it(emvCard.toCardScanningResult())
                }
                return emvCard.toCardScanningResult()
            }
        }
        return null
    }

    private fun EmvCard.toCardScanningResult() = CardScanningResult(
        cardNumber = if (demoVideoMode) fakeCardNumber else cardNumber,
        cardHolder = getFormattedCardHolder(),
        expirationDate = if (demoVideoMode) fakeCardExpDate else expireDate,
    )

    private fun EmvCard.getFormattedCardHolder(): String? {
        return listOfNotNull(holderFirstname, holderLastname)
            .joinToString(" ")
            .ifEmpty { null }
    }
}
