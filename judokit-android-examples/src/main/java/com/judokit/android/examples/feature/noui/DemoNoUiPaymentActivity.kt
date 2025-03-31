package com.judokit.android.examples.feature.noui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.judokit.android.examples.R
import com.judokit.android.examples.common.parcelable
import com.judokit.android.examples.databinding.ActivityDemoNoUiPaymentBinding
import com.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.service.CardTransactionManagerResultListener
import com.judopay.judokit.android.ui.common.ButtonState

sealed class ActivityState {
    object Idle : ActivityState()

    object PayWithCard : ActivityState()

    object PreAuthWithCard : ActivityState()

    object CheckCard : ActivityState()
}

class DemoNoUiPaymentActivity : AppCompatActivity(), CardTransactionManagerResultListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var service: JudoApiService
    private lateinit var transactionDetailsBuilder: TransactionDetails.Builder
    private val caller = DemoNoUiPaymentActivity::class.java.name
    private lateinit var binding: ActivityDemoNoUiPaymentBinding

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoNoUiPaymentBinding.inflate(layoutInflater)
        title = getString(R.string.feature_title_payment_no_ui)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val judo = intent.parcelable<Judo>(JUDO_OPTIONS)
        check(judo != null) { "Judo object is required" }

        judo.address?.administrativeDivision = ""
        service = JudoApiServiceFactory.create(this, judo)
        transactionDetailsBuilder =
            TransactionDetails.Builder()
                .setEmail(judo.emailAddress)
                .setCountryCode(judo.address?.countryCode.toString())
                .setPhoneCountryCode(judo.phoneCountryCode)
                .setMobileNumber(judo.mobileNumber)
                .setAddressLine1(judo.address?.line1)
                .setAddressLine2(judo.address?.line2)
                .setAddressLine3(judo.address?.line3)
                .setCity(judo.address?.town)
                .setPostalCode(judo.address?.postCode)
                .setAdministrativeDivision(judo.address?.state)
                // Setting card details manually, as it's for 'no-UI' transaction.
                .setCardNumber("4000023104662535")
                .setExpirationDate("12/25")
                .setCardHolderName("CHALLENGE")
                .setSecurityNumber("452")

        binding.payWithCardButton.setOnClickListener {
            handleState(ActivityState.PayWithCard)
            CardTransactionManager.getInstance(this).payment(
                transactionDetailsBuilder.build(),
                caller,
            )
        }

        binding.preAuthWithCardButton.setOnClickListener {
            handleState(ActivityState.PreAuthWithCard)
            CardTransactionManager.getInstance(this).preAuth(
                transactionDetailsBuilder.build(),
                caller,
            )
        }

        binding.checkCardButton.setOnClickListener {
            handleState(ActivityState.CheckCard)
            CardTransactionManager.getInstance(this).check(
                transactionDetailsBuilder.build(),
                caller,
            )
        }

        CardTransactionManager.getInstance(this).configureWith(judo)
        CardTransactionManager.getInstance(this).registerResultListener(this)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            setResult(resultCode, data)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        CardTransactionManager.getInstance(this).unRegisterResultListener(this)
        super.onDestroy()
    }

    override fun onCardTransactionResult(result: JudoPaymentResult) {
        runOnUiThread { handleState(ActivityState.Idle) }
        setResult(result.code, result.toIntent())
        finish()
    }

    private fun handleState(state: ActivityState) {
        when (state) {
            is ActivityState.Idle -> {
                binding.payWithCardButton.state = ButtonState.Enabled(R.string.feature_title_payment)
                binding.preAuthWithCardButton.state = ButtonState.Enabled(R.string.feature_title_preauth)
                binding.checkCardButton.state = ButtonState.Enabled(R.string.feature_title_check_card)
            }
            is ActivityState.PayWithCard -> {
                binding.payWithCardButton.state = ButtonState.Loading
                binding.preAuthWithCardButton.state = ButtonState.Disabled(R.string.feature_title_preauth)
                binding.checkCardButton.state = ButtonState.Disabled(R.string.feature_title_check_card)
            }
            is ActivityState.PreAuthWithCard -> {
                binding.payWithCardButton.state = ButtonState.Disabled(R.string.feature_title_payment)
                binding.preAuthWithCardButton.state = ButtonState.Loading
                binding.checkCardButton.state = ButtonState.Disabled(R.string.feature_title_check_card)
            }
            is ActivityState.CheckCard -> {
                binding.payWithCardButton.state = ButtonState.Disabled(R.string.feature_title_payment)
                binding.preAuthWithCardButton.state = ButtonState.Disabled(R.string.feature_title_preauth)
                binding.checkCardButton.state = ButtonState.Loading
            }
        }
    }
}
