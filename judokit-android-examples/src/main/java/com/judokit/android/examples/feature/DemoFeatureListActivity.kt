package com.judokit.android.examples.feature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.judokit.android.examples.R
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.common.toResult
import com.judokit.android.examples.feature.adapter.DemoFeaturesAdapter
import com.judokit.android.examples.feature.noui.DemoNoUiPaymentActivity
import com.judokit.android.examples.feature.paybybank.PayByBankActivity
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.examples.settings.SettingsActivity
import com.judopay.judokit.android.JUDO_ERROR
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JUDO_RESULT
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity
import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.Authorization
import com.judopay.judokit.android.api.model.BasicAuthorization
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.GooglePayConfiguration
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.PBBAConfiguration
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.model.USER_CANCELLED
import com.judopay.judokit.android.model.UiConfiguration
import com.judopay.judokit.android.model.googlepay.GooglePayAddressFormat
import com.judopay.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import com.judopay.judokit.android.ui.common.BR_PBBA_RESULT
import com.judopay.judokit.android.ui.common.PBBA_RESULT
import com.judopay.judokit.android.ui.common.isBankingAppAvailable
import kotlinx.android.synthetic.main.activity_demo_feature_list.*
import kotlinx.android.synthetic.main.dialog_get_transaction.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

const val JUDO_PAYMENT_WIDGET_REQUEST_CODE = 1
const val LAST_USED_WIDGET_TYPE_KEY = "LAST_USED_WIDGET_TYPE"

class DemoFeatureListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var deepLinkIntent = intent

    private val orderIdReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val result = intent?.getParcelableExtra<JudoResult>(PBBA_RESULT)
            // Handle result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            orderIdReceiver,
            IntentFilter(BR_PBBA_RESULT)
        )

        JudoApiServiceFactory.externalInterceptors = listOf(
            ChuckerInterceptor.Builder(this)
                .collector(ChuckerCollector(this))
                .maxContentLength(250000L)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build()
        )

        setContentView(R.layout.activity_demo_feature_list)
        setupRecyclerView()

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        deepLinkIfNeeded()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo_feature_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            when (resultCode) {
                PAYMENT_SUCCESS -> {
                    val result = data?.getParcelableExtra<JudoResult>(JUDO_RESULT)
                    processSuccessfulPayment(result)
                }

                PAYMENT_CANCELLED,
                PAYMENT_ERROR -> {
                    val error = data?.getParcelableExtra<JudoError>(JUDO_ERROR)
                    processPaymentError(error)
                }
            }
        }
        deepLinkIntent = intent
    }

    override fun onNewIntent(intent: Intent?) {
        deepLinkIntent = intent
        deepLinkIfNeeded()
        super.onNewIntent(intent)
    }

    private fun deepLinkIfNeeded() = deepLinkIntent?.data?.let {
        val newIntent = Intent(this, JudoActivity::class.java)
        val lastUsedPaymentWidget = sharedPreferences.getString(
            LAST_USED_WIDGET_TYPE_KEY,
            null
        ) ?: PaymentWidgetType.PAYMENT_METHODS.name

        newIntent.putExtra(JUDO_OPTIONS, getJudo(PaymentWidgetType.valueOf(lastUsedPaymentWidget)))
        startActivityForResult(newIntent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
    }

    private fun processSuccessfulPayment(result: JudoResult?) {
        if (result != null) {
            startResultActivity(result.toResult())
        } else {
            presentError("Unexpected null result object")
        }
    }

    private fun processPaymentError(error: JudoError?) {
        if (error != null) {
            if (error.code == USER_CANCELLED && error.details.isEmpty()) {
                toast("User cancelled the payment.")
            } else {
                startResultActivity(error.toResult())
            }
        } else {
            presentError("Unexpected null error object")
        }
    }

    private fun presentError(message: String) {
        if (isFinishing || isDestroyed) {
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_error_title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_button_ok, null)
            .show()
    }

    private fun showcaseFeature(feature: DemoFeature) {
        try {
            val widgetType = when (feature) {
                DemoFeature.GET_TRANSACTION_DETAILS,
                DemoFeature.PAYMENT, DemoFeature.NO_UI -> PaymentWidgetType.CARD_PAYMENT
                DemoFeature.PREAUTH -> PaymentWidgetType.PRE_AUTH
                DemoFeature.REGISTER_CARD -> PaymentWidgetType.REGISTER_CARD
                DemoFeature.CREATE_CARD_TOKEN -> PaymentWidgetType.CREATE_CARD_TOKEN
                DemoFeature.CHECK_CARD -> PaymentWidgetType.CHECK_CARD
                DemoFeature.PAYMENT_METHODS -> PaymentWidgetType.PAYMENT_METHODS
                DemoFeature.PREAUTH_PAYMENT_METHODS -> PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS
                DemoFeature.SERVER_TO_SERVER_PAYMENT_METHODS -> PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
                DemoFeature.GOOGLE_PAY_PAYMENT -> PaymentWidgetType.GOOGLE_PAY
                DemoFeature.GOOGLE_PAY_PREAUTH -> PaymentWidgetType.PRE_AUTH_GOOGLE_PAY
                DemoFeature.PAY_BY_BANK_APP -> {
                    if (isBankingAppAvailable(this)) {
                        PaymentWidgetType.PAY_BY_BANK_APP
                    } else {
                        throw IllegalStateException("Banking app not available")
                    }
                }
            }
            val judoConfig = getJudo(widgetType)
            navigateToJudoPaymentWidgetWithConfigurations(judoConfig, feature)
            sharedPreferences.edit().putString(LAST_USED_WIDGET_TYPE_KEY, widgetType.name).apply()
        } catch (exception: Exception) {
            when (exception) {
                is IllegalArgumentException, is IllegalStateException -> {
                    val message = exception.message
                        ?: "An error occurred, please check your settings."
                    toast("Error: $message")
                }
                else -> throw exception
            }
        }
    }

    private fun navigateToJudoPaymentWidgetWithConfigurations(judo: Judo, feature: DemoFeature) {
        if (feature == DemoFeature.GET_TRANSACTION_DETAILS) {
            showGetTransactionDialog(judo)
        } else {
            val myClass = when (judo.paymentWidgetType) {
                PaymentWidgetType.CARD_PAYMENT ->
                    if (feature == DemoFeature.NO_UI) {
                        DemoNoUiPaymentActivity::class.java
                    } else {
                        JudoActivity::class.java
                    }
                PaymentWidgetType.PAY_BY_BANK_APP -> PayByBankActivity::class.java
                else -> JudoActivity::class.java
            }
            val intent = Intent(this, myClass)
            intent.putExtra(JUDO_OPTIONS, judo)
            startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
        }
    }

    private fun toast(message: String) =
        Snackbar.make(sampleAppConstraintLayout, message, Snackbar.LENGTH_SHORT).show()

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        recyclerView.apply {
            addItemDecoration(dividerItemDecoration)
            adapter = DemoFeaturesAdapter(DemoFeature.values().asList()) {
                showcaseFeature(it)
            }
        }
    }

    private fun showGetTransactionDialog(judo: Judo) {
        val view = layoutInflater.inflate(R.layout.dialog_get_transaction, null)
        val service = JudoApiServiceFactory.createApiService(this, judo)
        AlertDialog.Builder(this).setTitle(R.string.feature_title_get_transaction_details)
            .setView(view)
            .setPositiveButton(
                R.string.dialog_button_ok
            ) { dialog, _ ->
                val fetchTransactionDetailsCallback =
                    object : Callback<JudoApiCallResult<Receipt>> {
                        override fun onResponse(
                            call: Call<JudoApiCallResult<Receipt>>,
                            response: Response<JudoApiCallResult<Receipt>>
                        ) {
                            when (val result = response.body()) {
                                is JudoApiCallResult.Success -> {
                                    processSuccessfulPayment(result.data?.toJudoResult())
                                }
                                is JudoApiCallResult.Failure -> {
                                    processPaymentError(result.error?.toJudoError())
                                }
                            }
                            dialog.dismiss()
                        }

                        override fun onFailure(
                            call: Call<JudoApiCallResult<Receipt>>,
                            t: Throwable
                        ) {
                            dialog.dismiss()
                            throw Exception(t)
                        }
                    }
                service.fetchTransactionWithReceiptId(view.receiptIdEditText.text.toString())
                    .enqueue(fetchTransactionDetailsCallback)
                view.receiptProgressBar.visibility = View.VISIBLE
                view.receiptIdEditText.visibility = View.GONE
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }.show()
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun getJudo(widgetType: PaymentWidgetType): Judo {

        val isSandboxed = sharedPreferences.getBoolean("is_sandboxed", true)
        val judoId = sharedPreferences.getString("judo_id", null)
        val initialRecurringPayment =
            sharedPreferences.getBoolean("is_initial_recurring_payment", false)
        val mobileNumber = sharedPreferences.getString("mobile_number", null)
        val phoneCountryCode = sharedPreferences.getString("phone_country_code", null)
        val emailAddress = sharedPreferences.getString("email_address", null)
        val challengeRequestIndicator = sharedPreferences.getString("challengeRequestIndicator", null)?.let { ChallengeRequestIndicator.valueOf(it) }
        val scaExemption = sharedPreferences.getString("scaExemption", null)?.let { ScaExemption.valueOf(it) }
        val threeDSTwoMaxTimeout = sharedPreferences.getString("threeDSTwoMaxTimeout", null)?.toInt()
        val address = cardAddress
        val accountDetails = primaryAccountDetails

        val builder = Judo.Builder(widgetType)
            .setJudoId(judoId)
            .setAuthorization(authorization)
            .setAmount(amount)
            .setReference(reference)
            .setIsSandboxed(isSandboxed)
            .setSupportedCardNetworks(networks)
            .setPaymentMethods(paymentMethods)
            .setUiConfiguration(uiConfiguration)
            .setGooglePayConfiguration(googlePayConfiguration)
            .setPBBAConfiguration(pbbaConfiguration)
            .setInitialRecurringPayment(initialRecurringPayment)
            .setMobileNumber(if (mobileNumber.isNullOrBlank()) null else mobileNumber)
            .setPhoneCountryCode(if (phoneCountryCode.isNullOrBlank()) null else phoneCountryCode)
            .setEmailAddress(if (emailAddress.isNullOrBlank()) null else emailAddress)
            .setChallengeRequestIndicator(challengeRequestIndicator)
            .setScaExemption(scaExemption)
            .setThreeDSTwoMaxTimeout(threeDSTwoMaxTimeout)
            .setNetworkTimeout(networkTimeout)

        if (address != null) {
            builder.setAddress(address)
        }

        if (accountDetails != null) {
            builder.setPrimaryAccountDetails(primaryAccountDetails)
        }

        return builder.build()
    }

    private val cardAddress: Address?
        get() {
            val isAddressEnabled = sharedPreferences.getBoolean("is_address_enabled", false)
            if (isAddressEnabled) {
                return Address.Builder()
                    .setLine1(sharedPreferences.getString("address_line_1", null))
                    .setLine2(sharedPreferences.getString("address_line_2", null))
                    .setLine3(sharedPreferences.getString("address_line_3", null))
                    .setTown(sharedPreferences.getString("address_town", null))
                    .setPostCode(sharedPreferences.getString("address_post_code", null))
                    .setCountryCode(sharedPreferences.getString("address_country_code", null)?.toIntOrNull())
                    .setBillingCountry(sharedPreferences.getString("address_billing_country", null))
                    .build()
            }

            return null
        }

    private val uiConfiguration: UiConfiguration
        get() {
            val isAVSEnabled = sharedPreferences.getBoolean("is_avs_enabled", false)
            val shouldDisplayAmount = sharedPreferences.getBoolean("should_display_amount", true)
            val shouldPaymentMethodsVerifySecurityCode =
                sharedPreferences.getBoolean("should_payment_methods_verify_security_code", true)
            val shouldPaymentButtonDisplayAmount =
                sharedPreferences.getBoolean("should_payment_button_display_amount", false)
            val shouldAskForBillingInformation = sharedPreferences.getBoolean("should_ask_for_billing_information", false)

            return UiConfiguration.Builder()
                .setAvsEnabled(isAVSEnabled)
                .setShouldPaymentMethodsDisplayAmount(shouldDisplayAmount)
                .setShouldPaymentMethodsVerifySecurityCode(shouldPaymentMethodsVerifySecurityCode)
                .setShouldPaymentButtonDisplayAmount(shouldPaymentButtonDisplayAmount)
                .setShouldAskForBillingInformation(shouldAskForBillingInformation)
                .build()
        }

    private val networks: Array<CardNetwork>?
        get() = sharedPreferences.getStringSet("supported_networks", null)
            ?.map { CardNetwork.valueOf(it) }
            ?.toTypedArray()

    private val paymentMethods: Array<PaymentMethod>
        get() {
            val paymentMethods =
                sharedPreferences.getStringSet("payment_methods", null)
                    ?.map { PaymentMethod.valueOf(it) }
                    ?.toTypedArray()

            // for demo purposes we want them in a certain order
            val methods = paymentMethods?.toList() ?: emptyList()
            return methods.sortedBy { it.ordinal }.toTypedArray()
        }

    private val reference: Reference
        get() {
            val isPaymentSessionEnabled =
                sharedPreferences.getBoolean("is_payment_session_enabled", false)

            val paymentReference = if (isPaymentSessionEnabled) {
                sharedPreferences.getString("payment_reference", null)
            } else {
                UUID.randomUUID().toString()
            }

            return Reference.Builder()
                .setConsumerReference("my-unique-ref")
                .setPaymentReference(paymentReference)
                .build()
        }

    private val amount: Amount
        get() {
            val amountValue = sharedPreferences.getString("amount", null)
            val currency = sharedPreferences.getString("currency", null)
            val myCurrency = if (!currency.isNullOrEmpty()) {
                Currency.valueOf(currency)
            } else Currency.GBP

            return Amount.Builder()
                .setAmount(amountValue)
                .setCurrency(myCurrency)
                .build()
        }

    private val googlePayConfiguration: GooglePayConfiguration
        get() {
            var billingAddressParams: GooglePayBillingAddressParameters? = null
            var shippingAddressParams: GooglePayShippingAddressParameters? = null

            val isProductionGooglePayEnv =
                sharedPreferences.getBoolean("is_google_pay_production_environment", false)

            val gPayEnv =
                if (isProductionGooglePayEnv) GooglePayEnvironment.PRODUCTION else GooglePayEnvironment.TEST

            val billingAddress = sharedPreferences.getString("billing_address", "NONE")
            val isShippingAddressRequired =
                sharedPreferences.getBoolean("is_shipping_address_required", false)
            val isShippingAddressPhoneNumberRequired =
                sharedPreferences.getBoolean("is_shipping_address_phone_number_required", false)
            val isBillingAddressPhoneNumberRequired =
                sharedPreferences.getBoolean("is_billing_address_phone_number_required", false)
            val isEmailAddressRequired =
                sharedPreferences.getBoolean("is_email_address_required", false)
            val countryCode =
                sharedPreferences.getString("google_pay_country_code", "GB")

            val isBillingAddressRequired = billingAddress != null && billingAddress != "NONE"

            if (isBillingAddressRequired) {
                billingAddressParams = GooglePayBillingAddressParameters(
                    format = GooglePayAddressFormat.valueOf(billingAddress!!),
                    phoneNumberRequired = isBillingAddressPhoneNumberRequired
                )
            }

            if (isShippingAddressRequired) {
                shippingAddressParams = GooglePayShippingAddressParameters(
                    phoneNumberRequired = isShippingAddressPhoneNumberRequired
                )
            }

            return GooglePayConfiguration.Builder()
                .setTransactionCountryCode(countryCode)
                .setEnvironment(gPayEnv)
                .setIsEmailRequired(isEmailAddressRequired)
                .setIsBillingAddressRequired(isBillingAddressRequired)
                .setBillingAddressParameters(billingAddressParams)
                .setIsShippingAddressRequired(isShippingAddressRequired)
                .setShippingAddressParameters(shippingAddressParams)
                .build()
        }

    private val primaryAccountDetails: PrimaryAccountDetails?
        get() {
            val isPrimaryAccountDetailsEnabled =
                sharedPreferences.getBoolean("is_primary_account_details_enabled", false)
            if (isPrimaryAccountDetailsEnabled) {
                return PrimaryAccountDetails.Builder()
                    .setName(sharedPreferences.getString("primary_account_name", null))
                    .setAccountNumber(
                        sharedPreferences.getString(
                            "primary_account_account_number",
                            null
                        )
                    )
                    .setDateOfBirth(
                        sharedPreferences.getString(
                            "primary_account_date_of_birth",
                            null
                        )
                    )
                    .setPostCode(sharedPreferences.getString("primary_account_post_code", null))
                    .build()
            }
            return null
        }

    private val pbbaConfiguration: PBBAConfiguration
        get() = PBBAConfiguration.Builder().setDeepLinkScheme("judo://pay")
            .setDeepLinkURL(deepLinkIntent?.data).build()

    private val authorization: Authorization
        get() {
            val token = sharedPreferences.getString("token", null)
            val secret = sharedPreferences.getString("secret", null)
            val isPaymentSessionEnabled =
                sharedPreferences.getBoolean("is_payment_session_enabled", false)

            return if (isPaymentSessionEnabled) {
                val paymentSession = sharedPreferences.getString("payment_session", null)
                PaymentSessionAuthorization.Builder()
                    .setPaymentSession(paymentSession)
                    .setApiToken(token)
                    .build()
            } else {
                BasicAuthorization.Builder()
                    .setApiToken(token)
                    .setApiSecret(secret)
                    .build()
            }
        }

    private val networkTimeout: NetworkTimeout
        get() {
            val connectTimeout = sharedPreferences.getString("connect_timeout", null)
            val readTimeout = sharedPreferences.getString("read_timeout", null)
            val writeTimeout = sharedPreferences.getString("write_timeout", null)

            return NetworkTimeout.Builder()
                .setConnectTimeout(connectTimeout?.toLongOrNull())
                .setReadTimeout(readTimeout?.toLongOrNull())
                .setWriteTimeout(writeTimeout?.toLongOrNull())
                .build()
        }
}
