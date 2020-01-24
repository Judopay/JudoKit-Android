package com.judopay.validation

import android.widget.EditText
import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.model.LuhnCheck
import com.judopay.view.SimpleTextWatcher
import io.reactivex.Observable
import io.reactivex.ObservableEmitter


class CardNumberValidator(
    private val editText: EditText,
    private val maestroSupported: Boolean,
    private val amexSupported: Boolean,
    private val discoverSupported: Boolean,
    private val unionPaySupported: Boolean,
    private val jcbSupported: Boolean
) : Validator {
    override fun onValidate(): Observable<Validation> =
        Observable.create { emitter: ObservableEmitter<Validation> ->
            emitter.onNext(getValidation(editText.text.toString().replace("\\s+".toRegex(), "")))
            editText.addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(text: CharSequence) {
                    emitter.onNext(getValidation(text.toString().replace("\\s+".toRegex(), "")))
                }
            })
        }

    private fun getValidation(cardNumber: String): Validation {
        val cardType = CardNetwork.fromCardNumber(cardNumber)
        val cardNumberLengthValid = isCardNumberLengthValid(cardNumber, cardType)
        val maestroAndNotSupported = isMaestroNotSupported(cardType, maestroSupported)
        val amexAndNotSupported = isAmexNotSupported(cardType, amexSupported)
        val discoverNotSupported = isDiscoverNotSupported(cardType, discoverSupported)
        val unionPayNotSupported = isUnionPayNotSupported(cardType, unionPaySupported)
        val jcbNotSupported = isJcbNotSupported(cardType, jcbSupported)
        val valid = isCardNumberValid(cardNumber, cardType) && cardNumberLengthValid
        var showError = !valid
                && (cardNumberLengthValid
                || maestroAndNotSupported
                || amexAndNotSupported)
        var error = 0
        when {
            maestroAndNotSupported -> {
                error = R.string.error_maestro_not_supported
                showError = true
            }
            amexAndNotSupported -> {
                error = R.string.error_amex_not_supported
                showError = true
            }
            discoverNotSupported -> {
                error = R.string.error_discover_not_supported
                showError = true
            }
            unionPayNotSupported -> {
                error = R.string.error_union_pay_not_supported
                showError = true
            }
            jcbNotSupported -> {
                error = R.string.error_jcb_not_supported
                showError = true
            }
            !valid -> error = R.string.check_card_number
        }
        return Validation(valid, error, showError)
    }

    private fun isAmexNotSupported(
        cardType: Int,
        amexSupported: Boolean
    ): Boolean = cardType == CardNetwork.AMEX && !amexSupported


    private fun isDiscoverNotSupported(
        cardType: Int,
        discoverSupported: Boolean
    ): Boolean = cardType == CardNetwork.DISCOVER && !discoverSupported


    private fun isUnionPayNotSupported(
        cardType: Int,
        unionPaySupported: Boolean
    ): Boolean = cardType == CardNetwork.CHINA_UNION_PAY && !unionPaySupported


    private fun isJcbNotSupported(cardType: Int, jcbSupported: Boolean): Boolean =
        cardType == CardNetwork.JCB && !jcbSupported


    private fun isMaestroNotSupported(
        cardType: Int,
        maestroSupported: Boolean
    ): Boolean = cardType == CardNetwork.MAESTRO && !maestroSupported

    private fun isCardNumberValid(cardNumber: String, cardType: Int): Boolean =
        (LuhnCheck.isValid(cardNumber)
                && ((cardType != CardNetwork.MAESTRO || maestroSupported)
                && (cardType != CardNetwork.AMEX || amexSupported)
                && (cardType != CardNetwork.DISCOVER || discoverSupported)
                && (cardType != CardNetwork.CHINA_UNION_PAY || unionPaySupported)
                && (cardType != CardNetwork.JCB || jcbSupported)))

    private fun isCardNumberLengthValid(cardNumber: String, cardType: Int): Boolean =
        when (cardType) {
            CardNetwork.AMEX -> cardNumber.length == 15
            CardNetwork.DINERS_CLUB_INTERNATIONAL -> cardNumber.length == 14
            else -> cardNumber.length == 16
        }
}