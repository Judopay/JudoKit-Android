package com.judopay.samples.feature

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.judopay.JUDO_ERROR
import com.judopay.JUDO_OPTIONS
import com.judopay.JUDO_RESULT
import com.judopay.Judo
import com.judopay.JudoActivity
import com.judopay.PAYMENT_CANCELLED
import com.judopay.PAYMENT_ERROR
import com.judopay.PAYMENT_SUCCESS
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.model.Amount
import com.judopay.model.CardNetwork
import com.judopay.model.Currency
import com.judopay.model.GooglePayConfiguration
import com.judopay.model.JudoError
import com.judopay.model.JudoResult
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.Reference
import com.judopay.model.USER_CANCELLED
import com.judopay.model.UiConfiguration
import com.judopay.model.googlepay.GooglePayAddressFormat
import com.judopay.model.googlepay.GooglePayBillingAddressParameters
import com.judopay.model.googlepay.GooglePayEnvironment
import com.judopay.model.googlepay.GooglePayShippingAddressParameters
import com.judopay.samples.R
import com.judopay.samples.common.startResultActivity
import com.judopay.samples.common.toResult
import com.judopay.samples.feature.adapter.DemoFeaturesAdapter
import com.judopay.samples.model.DemoFeature
import com.judopay.samples.settings.SettingsActivity
import com.readystatesoftware.chuck.ChuckInterceptor
import kotlinx.android.synthetic.main.activity_demo_feature_list.*
import java.util.UUID

const val JUDO_PAYMENT_WIDGET_REQUEST_CODE = 1

class DemoFeatureListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        JudoApiServiceFactory.externalInterceptors = listOf(ChuckInterceptor(this))

        setContentView(R.layout.activity_demo_feature_list)
        setupRecyclerView()

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
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
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_error_title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_button_ok, null)
            .show()
    }

    private fun showcaseFeature(feature: DemoFeature) {
        try {
            val widgetType = when (feature) {
                DemoFeature.PAYMENT -> PaymentWidgetType.CARD_PAYMENT
                DemoFeature.PREAUTH -> PaymentWidgetType.PRE_AUTH
                DemoFeature.REGISTER_CARD -> PaymentWidgetType.REGISTER_CARD
                DemoFeature.CREATE_CARD_TOKEN -> PaymentWidgetType.CREATE_CARD_TOKEN
                DemoFeature.CHECK_CARD -> PaymentWidgetType.CHECK_CARD
                DemoFeature.PAYMENT_METHODS -> PaymentWidgetType.PAYMENT_METHODS
                DemoFeature.PREAUTH_PAYMENT_METHODS -> PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS
                DemoFeature.SERVER_TO_SERVER_PAYMENT_METHODS -> PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
                DemoFeature.GOOGLE_PAY_PAYMENT -> PaymentWidgetType.GOOGLE_PAY
                DemoFeature.GOOGLE_PAY_PREAUTH -> PaymentWidgetType.PRE_AUTH_GOOGLE_PAY
            }
            val judoConfig = getJudo(widgetType)
            navigateToJudoPaymentWidgetWithConfigurations(judoConfig)
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

    private fun navigateToJudoPaymentWidgetWithConfigurations(judo: Judo) {
        val intent = Intent(this, JudoActivity::class.java)
        intent.putExtra(JUDO_OPTIONS, judo)
        startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
    }

    private fun toast(message: String) =
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        recyclerView.apply {
            addItemDecoration(dividerItemDecoration)
            adapter = DemoFeaturesAdapter(DemoFeature.values().asList()) {
                showcaseFeature(it)
            }
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun getJudo(widgetType: PaymentWidgetType): Judo {

        val isSandboxed = sharedPreferences.getBoolean("is_sandboxed", true)
        val judoId = sharedPreferences.getString("judo_id", null)
        val siteId = sharedPreferences.getString("site_id", null)
        val token = sharedPreferences.getString("token", null)
        val secret = sharedPreferences.getString("secret", null)

        return Judo.Builder(widgetType)
            .setJudoId(judoId)
            .setSiteId(siteId)
            .setApiToken(token)
            .setApiSecret(secret)
            .setAmount(amount)
            .setReference(reference)
            .setIsSandboxed(isSandboxed)
            .setSupportedCardNetworks(networks)
            .setPaymentMethods(paymentMethods)
            .setUiConfiguration(uiConfiguration)
            .setGooglePayConfiguration(googlePayConfiguration)
            .build()
    }

    private val uiConfiguration: UiConfiguration
        get() {
            val isAVSEnabled = sharedPreferences.getBoolean("is_avs_enabled", false)
            val shouldDisplayAmount = sharedPreferences.getBoolean("should_display_amount", true)
            return UiConfiguration.Builder()
                .setAvsEnabled(isAVSEnabled)
                .setShouldDisplayAmount(shouldDisplayAmount)
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
            val randomString = UUID.randomUUID().toString()

            return Reference.Builder()
                .setConsumerReference("my-unique-ref")
                .setPaymentReference(randomString)
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
                .setTransactionCountryCode("GB")
                .setEnvironment(gPayEnv)
                .setIsEmailRequired(isEmailAddressRequired)
                .setIsBillingAddressRequired(isBillingAddressRequired)
                .setBillingAddressParameters(billingAddressParams)
                .setIsShippingAddressRequired(isShippingAddressRequired)
                .setShippingAddressParameters(shippingAddressParams)
                .build()
        }
}
