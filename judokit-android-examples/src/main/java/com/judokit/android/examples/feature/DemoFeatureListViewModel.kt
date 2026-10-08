package com.judokit.android.examples.feature

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.judokit.android.examples.common.toResult
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.examples.model.Result
import com.judopay.judo3ds2.customization.ButtonCustomization
import com.judopay.judo3ds2.customization.LabelCustomization
import com.judopay.judo3ds2.customization.TextBoxCustomization
import com.judopay.judo3ds2.customization.ToolbarCustomization
import com.judopay.judo3ds2.customization.UiCustomization
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.Authorization
import com.judopay.judokit.android.api.model.BasicAuthorization
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.GooglePayConfiguration
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.model.RecommendationConfiguration
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.model.USER_CANCELLED
import com.judopay.judokit.android.model.UiConfiguration
import com.judopay.judokit.android.model.googlepay.GooglePayAddressFormat
import com.judopay.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judopay.judokit.android.model.googlepay.GooglePayCheckoutOption
import com.judopay.judokit.android.model.googlepay.GooglePayDeferredParameters
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayIntroductoryPeriodInfo
import com.judopay.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriod
import com.judopay.judokit.android.model.googlepay.GooglePayRecurrencePeriodItem
import com.judopay.judokit.android.model.googlepay.GooglePayRecurringParameters
import com.judopay.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.UUID

const val LAST_USED_WIDGET_TYPE_KEY = "LAST_USED_WIDGET_TYPE"

sealed class DemoFeatureListEffect {
    data class LaunchJudoWidget(
        val judo: Judo,
    ) : DemoFeatureListEffect()

    data class LaunchNoUiPayment(
        val judo: Judo,
    ) : DemoFeatureListEffect()

    data class LaunchTokenPayments(
        val judo: Judo,
    ) : DemoFeatureListEffect()

    object ShowGetTransactionDialog : DemoFeatureListEffect()

    data class ShowResult(
        val result: Result,
    ) : DemoFeatureListEffect()

    data class ShowError(
        val message: String,
    ) : DemoFeatureListEffect()

    object ShowUserCancelled : DemoFeatureListEffect()
}

private fun DemoFeature.toWidgetType(): PaymentWidgetType =
    when (this) {
        DemoFeature.GET_TRANSACTION_DETAILS,
        DemoFeature.PAYMENT,
        DemoFeature.NO_UI,
        -> PaymentWidgetType.CARD_PAYMENT
        DemoFeature.PREAUTH -> PaymentWidgetType.PRE_AUTH
        DemoFeature.CREATE_CARD_TOKEN -> PaymentWidgetType.CREATE_CARD_TOKEN
        DemoFeature.CHECK_CARD -> PaymentWidgetType.CHECK_CARD
        DemoFeature.PAYMENT_METHODS -> PaymentWidgetType.PAYMENT_METHODS
        DemoFeature.PREAUTH_PAYMENT_METHODS -> PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS
        DemoFeature.SERVER_TO_SERVER_PAYMENT_METHODS -> PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
        DemoFeature.GOOGLE_PAY_PAYMENT -> PaymentWidgetType.GOOGLE_PAY
        DemoFeature.GOOGLE_PAY_PREAUTH -> PaymentWidgetType.PRE_AUTH_GOOGLE_PAY
        DemoFeature.TOKEN_PAYMENTS -> PaymentWidgetType.TOKEN_PAYMENT
    }

@Suppress("TooManyFunctions", "TooGenericExceptionCaught", "DEPRECATION")
class DemoFeatureListViewModel(
    app: Application,
) : AndroidViewModel(app) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(app)

    private val _effects = MutableSharedFlow<DemoFeatureListEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<DemoFeatureListEffect> = _effects.asSharedFlow()

    /** Retained so [fetchTransactionDetails] can create an API service without re-reading prefs. */
    private var currentJudo: Judo? = null

    fun onFeatureSelected(feature: DemoFeature) {
        try {
            val widgetType = feature.toWidgetType()
            val judo = buildJudo(widgetType)
            currentJudo = judo
            prefs.edit { putString(LAST_USED_WIDGET_TYPE_KEY, widgetType.name) }

            val effect =
                when {
                    feature == DemoFeature.GET_TRANSACTION_DETAILS -> DemoFeatureListEffect.ShowGetTransactionDialog
                    feature == DemoFeature.NO_UI -> DemoFeatureListEffect.LaunchNoUiPayment(judo)
                    judo.paymentWidgetType == PaymentWidgetType.TOKEN_PAYMENT -> DemoFeatureListEffect.LaunchTokenPayments(judo)
                    else -> DemoFeatureListEffect.LaunchJudoWidget(judo)
                }
            _effects.tryEmit(effect)
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException, is IllegalStateException ->
                    _effects.tryEmit(DemoFeatureListEffect.ShowError(e.message ?: "An error occurred, please check your settings."))
                else -> throw e
            }
        }
    }

    fun handleJudoPaymentResult(result: JudoPaymentResult) {
        when (result) {
            is JudoPaymentResult.Success -> {
                val judoResult = result.result
                _effects.tryEmit(DemoFeatureListEffect.ShowResult(judoResult.toResult()))
            }
            is JudoPaymentResult.Error -> emitPaymentError(result.error)
            is JudoPaymentResult.UserCancelled -> emitPaymentError(result.error)
        }
    }

    fun fetchTransactionDetails(receiptId: String) {
        val judo = currentJudo ?: return
        viewModelScope.launch {
            try {
                val service = JudoApiServiceFactory.createApiService(getApplication(), judo)
                when (val result = service.fetchTransactionWithReceiptId(receiptId).await()) {
                    is JudoApiCallResult.Success -> {
                        val judoResult = result.data?.toJudoResult()
                        if (judoResult != null) {
                            _effects.emit(DemoFeatureListEffect.ShowResult(judoResult.toResult()))
                        } else {
                            _effects.emit(DemoFeatureListEffect.ShowError("Unexpected null result object"))
                        }
                    }
                    is JudoApiCallResult.Failure -> {
                        val error = result.error?.toJudoError()
                        if (error != null) {
                            _effects.emit(
                                if (error.code == USER_CANCELLED && error.details.isEmpty()) {
                                    DemoFeatureListEffect.ShowUserCancelled
                                } else {
                                    DemoFeatureListEffect.ShowResult(error.toResult())
                                },
                            )
                        } else {
                            _effects.emit(DemoFeatureListEffect.ShowError("Unexpected null error object"))
                        }
                    }
                }
            } catch (e: Exception) {
                _effects.emit(DemoFeatureListEffect.ShowError(e.message ?: "An error occurred"))
            }
        }
    }

    private fun emitPaymentError(error: JudoError?) {
        if (error == null) {
            _effects.tryEmit(DemoFeatureListEffect.ShowError("Unexpected null error object"))
            return
        }
        _effects.tryEmit(
            if (error.code == USER_CANCELLED && error.details.isEmpty()) {
                DemoFeatureListEffect.ShowUserCancelled
            } else {
                DemoFeatureListEffect.ShowResult(error.toResult())
            },
        )
    }

    private inline fun <reified T : Enum<T>> parseEnumPref(key: String): T? =
        prefs.getString(key, null)?.let {
            try {
                enumValueOf<T>(it)
            } catch (throwable: Throwable) {
                _effects.tryEmit(DemoFeatureListEffect.ShowError("Error: ${throwable.message}"))
                null
            }
        }

    @SuppressLint("NewApi")
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun buildJudo(widgetType: PaymentWidgetType): Judo {
        val isSandboxed = prefs.getBoolean("is_sandboxed", true)
        val judoId = prefs.getString("judo_id", null)
        val initialRecurringPayment = prefs.getBoolean("is_initial_recurring_payment", false)
        val delayedAuthorisation = prefs.getBoolean("is_delayed_authorisation_on", false)
        val allowIncrement = prefs.getBoolean("is_allow_increment_on", false)
        val mobileNumber = prefs.getString("address_mobile_number", null)
        val phoneCountryCode = prefs.getString("address_phone_country_code", null)
        val emailAddress = prefs.getString("address_email_address", null)
        val challengeRequestIndicator = parseEnumPref<ChallengeRequestIndicator>("challenge_request_indicator")
        val scaExemption = parseEnumPref<ScaExemption>("sca_exemption")
        val threeDSTwoMaxTimeout = prefs.getString("three_ds_two_max_timeout", null)?.toInt()
        val messageVersion = prefs.getString("three_ds_two_message_version", null)
        val isDisableNetworkTokenisationOn = prefs.getBoolean("is_disable_network_tokenisation_on", false)

        val extras =
            Bundle().apply {
                putBoolean("shouldUseFabrickDsId", prefs.getBoolean("is_using_fabrick_3ds_service", false))
            }

        val builder =
            Judo
                .Builder(widgetType)
                .setJudoId(judoId)
                .setAuthorization(authorization)
                .setAmount(amount)
                .setReference(reference)
                .setIsSandboxed(isSandboxed)
                .setSupportedCardNetworks(networks)
                .setPaymentMethods(paymentMethods)
                .setUiConfiguration(uiConfiguration)
                .setGooglePayConfiguration(googlePayConfiguration)
                .setInitialRecurringPayment(initialRecurringPayment)
                .setDelayedAuthorisation(delayedAuthorisation)
                .setAllowIncrement(allowIncrement)
                .setMobileNumber(mobileNumber?.ifBlank { null })
                .setPhoneCountryCode(phoneCountryCode?.ifBlank { null })
                .setEmailAddress(emailAddress?.ifBlank { null })
                .setChallengeRequestIndicator(challengeRequestIndicator)
                .setScaExemption(scaExemption)
                .setThreeDSTwoMaxTimeout(threeDSTwoMaxTimeout)
                .setNetworkTimeout(networkTimeout)
                .setRecommendationConfiguration(recommendationConfiguration)
                .setDisableNetworkTokenisation(isDisableNetworkTokenisationOn)
                .setExtras(extras)

        if (!messageVersion.isNullOrBlank()) {
            builder.setThreeDSTwoMessageVersion(messageVersion)
        }

        cardAddress?.let { builder.setAddress(it) }
        primaryAccountDetails?.let { builder.setPrimaryAccountDetails(it) }

        return builder.build()
    }

    private val authorization: Authorization
        get() {
            val token = prefs.getString("token", null)
            val secret = prefs.getString("secret", null)
            val isPaymentSessionEnabled = prefs.getBoolean("is_payment_session_enabled", false)
            return if (isPaymentSessionEnabled) {
                PaymentSessionAuthorization
                    .Builder()
                    .setPaymentSession(prefs.getString("payment_session", null))
                    .setApiToken(token)
                    .build()
            } else {
                BasicAuthorization
                    .Builder()
                    .setApiToken(token)
                    .setApiSecret(secret)
                    .build()
            }
        }

    private val amount: Amount
        get() {
            val amountValue = prefs.getString("amount", null)
            val currency = prefs.getString("currency", null)
            val myCurrency = if (!currency.isNullOrEmpty()) Currency.valueOf(currency) else Currency.GBP
            return Amount
                .Builder()
                .setAmount(amountValue)
                .setCurrency(myCurrency)
                .build()
        }

    private val reference: Reference
        get() {
            val isPaymentSessionEnabled = prefs.getBoolean("is_payment_session_enabled", false)
            val paymentReference =
                if (isPaymentSessionEnabled) prefs.getString("payment_reference", null) else UUID.randomUUID().toString()
            return Reference
                .Builder()
                .setConsumerReference("my-unique-ref")
                .setPaymentReference(paymentReference)
                .setMetaData(Bundle().apply { putString("exampleMetaKey", "exampleMetaValue") })
                .build()
        }

    private val networks: Array<CardNetwork>?
        get() =
            prefs
                .getStringSet("supported_networks", null)
                ?.map { CardNetwork.valueOf(it) }
                ?.toTypedArray()

    private val paymentMethods: Array<PaymentMethod>
        get() {
            val methods =
                prefs
                    .getStringSet("payment_methods", null)
                    ?.mapNotNull {
                        try {
                            PaymentMethod.valueOf(it)
                        } catch (_: Exception) {
                            null
                        }
                    }?.toList() ?: emptyList()
            // Keep a consistent order for demo purposes.
            return methods.sortedBy { it.ordinal }.toTypedArray()
        }

    private val networkTimeout: NetworkTimeout
        get() =
            NetworkTimeout
                .Builder()
                .setConnectTimeout(prefs.getString("connect_timeout", null)?.toLongOrNull())
                .setReadTimeout(prefs.getString("read_timeout", null)?.toLongOrNull())
                .setWriteTimeout(prefs.getString("write_timeout", null)?.toLongOrNull())
                .build()

    private val cardAddress: Address?
        get() {
            if (!prefs.getBoolean("is_address_enabled", false)) return null
            return Address
                .Builder()
                .setLine1(prefs.getString("address_line_1", null))
                .setLine2(prefs.getString("address_line_2", null))
                .setLine3(prefs.getString("address_line_3", null))
                .setTown(prefs.getString("address_town", null))
                .setPostCode(prefs.getString("address_post_code", null))
                .setCountryCode(prefs.getString("address_country_code", null)?.toIntOrNull())
                .setAdministrativeDivision(prefs.getString("address_administrative_division", null))
                .setBillingCountry(prefs.getString("address_billing_country", null))
                .build()
        }

    private val primaryAccountDetails: PrimaryAccountDetails?
        get() {
            if (!prefs.getBoolean("is_primary_account_details_enabled", false)) return null
            return PrimaryAccountDetails
                .Builder()
                .setName(prefs.getString("primary_account_name", null))
                .setAccountNumber(prefs.getString("primary_account_account_number", null))
                .setDateOfBirth(prefs.getString("primary_account_date_of_birth", null))
                .setPostCode(prefs.getString("primary_account_post_code", null))
                .build()
        }

    private val recommendationConfiguration: RecommendationConfiguration?
        get() {
            if (!prefs.getBoolean("is_recommendation_enabled", false)) return null
            return RecommendationConfiguration
                .Builder()
                .setRsaPublicKey(prefs.getString("rsa_key", null))
                .setUrl(prefs.getString("recommendation_url", null))
                .setTimeout(prefs.getString("recommendation_timeout", null)?.toInt())
                .setShouldHaltTransactionInCaseOfAnyError(prefs.getBoolean("is_recommendation_halt_transaction_enabled", false))
                .build()
        }

    @Suppress("TooGenericExceptionCaught", "CyclomaticComplexMethod")
    private val uiConfiguration: UiConfiguration
        get() {
            val builder =
                UiConfiguration
                    .Builder()
                    .setAvsEnabled(prefs.getBoolean("is_avs_enabled", false))
                    .setShouldPaymentMethodsDisplayAmount(prefs.getBoolean("should_payment_methods_display_amount", true))
                    .setShouldPaymentMethodsVerifySecurityCode(prefs.getBoolean("should_payment_methods_verify_security_code", true))
                    .setShouldAskForCSC(prefs.getBoolean("should_ask_for_csc", false))
                    .setShouldAskForCardholderName(prefs.getBoolean("should_ask_for_cardholder_name", false))
                    .setShouldPaymentButtonDisplayAmount(prefs.getBoolean("should_payment_button_display_amount", false))
                    .setShouldAskForBillingInformation(prefs.getBoolean("should_ask_for_billing_information", false))
            try {
                builder.setThreeDSUiCustomization(threeDSUiCustomization)
            } catch (e: Throwable) {
                _effects.tryEmit(DemoFeatureListEffect.ShowError("Error: ${e.message}"))
            }
            return builder.build()
        }

    private val googlePayConfiguration: GooglePayConfiguration
        get() {
            fun stringPref(
                key: String,
                default: String,
            ): String = prefs.getString(key, default) ?: default

            val isProductionEnv = prefs.getBoolean("is_google_pay_production_environment", false)
            val gPayEnv = if (isProductionEnv) GooglePayEnvironment.PRODUCTION else GooglePayEnvironment.TEST
            val billingAddress = prefs.getString("billing_address", "NONE")
            val isBillingAddressRequired = billingAddress != null && billingAddress != "NONE"
            val isShippingAddressRequired = prefs.getBoolean("is_shipping_address_required", false)
            val shippingAddressAllowedCountries =
                prefs
                    .getString("google_pay_shipping_address_allowed_countries", null)
                    ?.ifEmpty { null }
                    ?.split(",")
                    ?.toTypedArray()
            val billingAddressParams =
                if (isBillingAddressRequired) {
                    GooglePayBillingAddressParameters(
                        format = GooglePayAddressFormat.valueOf(billingAddress ?: "FULL"),
                        phoneNumberRequired = prefs.getBoolean("is_billing_address_phone_number_required", false),
                    )
                } else {
                    null
                }
            val shippingAddressParams =
                if (isShippingAddressRequired) {
                    GooglePayShippingAddressParameters(
                        allowedCountryCodes = shippingAddressAllowedCountries,
                        phoneNumberRequired = prefs.getBoolean("is_shipping_address_phone_number_required", false),
                    )
                } else {
                    null
                }
            val totalPriceStatus = prefs.getString("google_pay_total_price_status", "FINAL")
            val checkoutOption = prefs.getString("google_pay_checkout_option", null)
            val mitType = stringPref("google_pay_mit_type", "NONE")
            val mitManagementUrl = prefs.getString("google_pay_mit_management_url", null)?.ifBlank { null }
            val mitBillingAgreement = prefs.getString("google_pay_mit_billing_agreement", null)?.ifBlank { null }
            val mitImmediateTotalPrice = stringPref("google_pay_mit_immediate_total_price", "0.00")
            val mitImmediateDisplayItems =
                GooglePayDisplayItemsParser.parse(
                    prefs.getString("google_pay_mit_immediate_display_items", null),
                )

            val deferredParameters =
                if (mitType == "DEFERRED") {
                    val deferredPriceStatus =
                        GooglePayPriceStatus.valueOf(
                            stringPref("google_pay_deferred_price_status", "FINAL"),
                        )
                    GooglePayDeferredParameters(
                        immediateTotalPrice = mitImmediateTotalPrice,
                        billingDateTime = stringPref("google_pay_deferred_billing_date_time", "2027-01-01T08:00:00Z"),
                        priceStatus = deferredPriceStatus,
                        price =
                            if (deferredPriceStatus == GooglePayPriceStatus.NOT_CURRENTLY_KNOWN) {
                                null
                            } else {
                                stringPref("google_pay_deferred_price", "200.00")
                            },
                        label = stringPref("google_pay_deferred_label", "Hotel Room Reservation"),
                        immediateDisplayItems = mitImmediateDisplayItems,
                        displayItems =
                            GooglePayDisplayItemsParser.parse(
                                prefs.getString("google_pay_deferred_display_items", null),
                            ),
                        managementUrl = mitManagementUrl,
                        billingAgreement = mitBillingAgreement,
                    )
                } else {
                    null
                }

            val recurringParameters =
                if (mitType == "RECURRING") {
                    val itemPriceStatus =
                        GooglePayPriceStatus.valueOf(
                            stringPref("google_pay_recurring_item_price_status", "FINAL"),
                        )
                    val recurrenceItem =
                        GooglePayRecurrencePeriodItem(
                            billingInitialDateTime =
                                prefs
                                    .getString("google_pay_recurring_item_billing_initial_date_time", null)
                                    ?.ifBlank { null },
                            billingFinalDateTime =
                                prefs
                                    .getString("google_pay_recurring_item_billing_final_date_time", null)
                                    ?.ifBlank { null },
                            label =
                                stringPref(
                                    "google_pay_recurring_item_label",
                                    "Premium Plan Monthly Subscription",
                                ),
                            price =
                                if (itemPriceStatus == GooglePayPriceStatus.NOT_CURRENTLY_KNOWN) {
                                    null
                                } else {
                                    stringPref("google_pay_recurring_item_price", "25.00")
                                },
                            priceStatus = itemPriceStatus,
                            displayItems =
                                GooglePayDisplayItemsParser.parse(
                                    prefs.getString("google_pay_recurring_item_display_items", null),
                                ),
                            recurrencePeriod =
                                GooglePayRecurrencePeriod.valueOf(
                                    stringPref("google_pay_recurring_item_period", "MONTH"),
                                ),
                            recurrencePeriodCount =
                                prefs
                                    .getString("google_pay_recurring_item_period_count", "1")
                                    ?.toIntOrNull()
                                    ?: 1,
                        )
                    val introductoryPeriodInfo =
                        if (prefs.getBoolean("is_google_pay_recurring_introductory_period_enabled", false)) {
                            GooglePayIntroductoryPeriodInfo(
                                introductoryPeriodStartDateTime =
                                    prefs
                                        .getString(
                                            "google_pay_recurring_introductory_period_start_date_time",
                                            null,
                                        )?.ifBlank { null },
                                introductoryPeriodEndDateTime =
                                    stringPref(
                                        "google_pay_recurring_introductory_period_end_date_time",
                                        "2026-08-01T08:00:00Z",
                                    ),
                                label =
                                    stringPref(
                                        "google_pay_recurring_introductory_period_label",
                                        "7 Day Free Trial",
                                    ),
                                totalPrice =
                                    stringPref(
                                        "google_pay_recurring_introductory_period_total_price",
                                        "0.00",
                                    ),
                                displayItems =
                                    GooglePayDisplayItemsParser.parse(
                                        prefs.getString(
                                            "google_pay_recurring_introductory_period_display_items",
                                            null,
                                        ),
                                    ),
                            )
                        } else {
                            null
                        }
                    GooglePayRecurringParameters(
                        immediateTotalPrice = mitImmediateTotalPrice,
                        recurrenceItems = listOf(recurrenceItem),
                        introductoryPeriodInfo = introductoryPeriodInfo,
                        immediateDisplayItems = mitImmediateDisplayItems,
                        managementUrl = mitManagementUrl,
                        billingAgreement = mitBillingAgreement,
                    )
                } else {
                    null
                }

            return GooglePayConfiguration
                .Builder()
                .setEnvironment(gPayEnv)
                .setMerchantName(prefs.getString("google_pay_merchant_name", null)?.trim()?.ifBlank { null })
                .setTransactionCountryCode(prefs.getString("google_pay_country_code", "GB"))
                .setTransactionId(prefs.getString("google_pay_transaction_id", null)?.ifBlank { null })
                .setTotalPriceStatus(totalPriceStatus?.let { GooglePayPriceStatus.valueOf(it) })
                .setTotalPriceLabel(prefs.getString("google_pay_total_price_label", null)?.ifEmpty { null })
                .setCheckoutOption(checkoutOption?.let { GooglePayCheckoutOption.valueOf(it) })
                .setIsEmailRequired(prefs.getBoolean("is_email_address_required", false))
                .setIsBillingAddressRequired(isBillingAddressRequired)
                .setBillingAddressParameters(billingAddressParams)
                .setIsShippingAddressRequired(isShippingAddressRequired)
                .setShippingAddressParameters(shippingAddressParams)
                .setAllowPrepaidCards(prefs.getBoolean("allow_prepaid_cards", true))
                .setAllowCreditCards(prefs.getBoolean("allow_credit_cards", true))
                .setDeferredParameters(deferredParameters)
                .setRecurringParameters(recurringParameters)
                .build()
        }

    private val threeDSUiCustomization: UiCustomization?
        get() {
            if (!prefs.getBoolean("three_ds_is_ui_customisation_enabled", false)) return null
            val customization = UiCustomization()

            customization.setToolbarCustomization(
                ToolbarCustomization().apply {
                    setTextFontName(prefs.getString("three_ds_toolbar_text_font_name", null))
                    setTextColor(prefs.getString("three_ds_toolbar_text_color", null))
                    setTextFontSize(prefs.getString("three_ds_toolbar_text_font_size", null)?.toInt())
                    setBackgroundColor(prefs.getString("three_ds_toolbar_background_color", null))
                    setHeaderText(prefs.getString("three_ds_toolbar_header_text", null))
                    setButtonText(prefs.getString("three_ds_toolbar_button_text", null))
                },
            )
            customization.setLabelCustomization(
                LabelCustomization().apply {
                    setTextFontName(prefs.getString("three_ds_label_text_font_name", null))
                    setTextColor(prefs.getString("three_ds_label_text_color", null))
                    setTextFontSize(prefs.getString("three_ds_label_text_font_size", null)?.toInt())
                    setHeadingTextFontName(prefs.getString("three_ds_label_heading_text_font_name", null))
                    setHeadingTextColor(prefs.getString("three_ds_label_heading_text_color", null))
                    setHeadingTextFontSize(prefs.getString("three_ds_label_heading_text_font_size", null)?.toInt())
                },
            )
            customization.setTextBoxCustomization(
                TextBoxCustomization().apply {
                    setTextFontName(prefs.getString("three_ds_text_box_text_font_name", null))
                    setTextColor(prefs.getString("three_ds_text_box_text_color", null))
                    setTextFontSize(prefs.getString("three_ds_text_box_text_font_size", null)?.toInt())
                    setBorderWidth(prefs.getString("three_ds_text_box_border_width", null)?.toInt())
                    setBorderColor(prefs.getString("three_ds_text_box_border_color", null))
                    setCornerRadius(prefs.getString("three_ds_text_box_corner_radius", null)?.toInt())
                },
            )

            for ((prefPrefix, buttonType) in listOf(
                "three_ds_submit_button" to UiCustomization.ButtonType.SUBMIT,
                "three_ds_next_button" to UiCustomization.ButtonType.NEXT,
                "three_ds_continue_button" to UiCustomization.ButtonType.CONTINUE,
                "three_ds_cancel_button" to UiCustomization.ButtonType.CANCEL,
                "three_ds_resend_button" to UiCustomization.ButtonType.RESEND,
            )) {
                customization.setButtonCustomization(
                    ButtonCustomization().apply {
                        setTextFontName(prefs.getString("${prefPrefix}_text_font_name", null))
                        setTextColor(prefs.getString("${prefPrefix}_text_color", null))
                        setTextFontSize(prefs.getString("${prefPrefix}_text_font_size", null)?.toInt())
                        setBackgroundColor(prefs.getString("${prefPrefix}_background_color", null))
                        setCornerRadius(prefs.getString("${prefPrefix}_corner_radius", null)?.toInt())
                    },
                    buttonType,
                )
            }

            return customization
        }
}
