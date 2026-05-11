package com.judokit.android.examples.feature.noui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.judokit.android.examples.R
import com.judokit.android.examples.common.parcelable
import com.judokit.android.examples.databinding.ActivityDemoNoUiPaymentBinding
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.code
import com.judopay.judokit.android.model.toIntent
import com.judopay.judokit.android.ui.common.ButtonState
import kotlinx.coroutines.launch

class DemoNoUiPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoNoUiPaymentBinding
    private lateinit var viewModel: DemoNoUiPaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityDemoNoUiPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val judo = requireNotNull(intent.parcelable<Judo>(JUDO_OPTIONS)) { "Judo object is required" }
        judo.address?.administrativeDivision = ""

        viewModel =
            ViewModelProvider(
                this,
                DemoNoUiPaymentViewModel.Factory(this, judo),
            )[DemoNoUiPaymentViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(binding.nestedScrollView) { view, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = maxOf(imeBottom, navBottom))
            insets
        }

        val initial = viewModel.initialCardInput
        binding.cardNumberEditText.setText(initial.cardNumber)
        binding.expirationDateEditText.setText(initial.expirationDate)
        binding.cardholderNameEditText.setText(initial.cardholderName)
        binding.securityNumberEditText.setText(initial.securityNumber)

        binding.payWithCardButton.setOnClickListener { viewModel.payment(this, currentCardInput()) }
        binding.preAuthWithCardButton.setOnClickListener { viewModel.preAuth(this, currentCardInput()) }
        binding.checkCardButton.setOnClickListener { viewModel.check(this, currentCardInput()) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect { handleState(it) } }
                launch { viewModel.paymentResult.collect { onTransactionResult(it) } }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    private fun currentCardInput() =
        CardInputState(
            cardNumber = binding.cardNumberEditText.text.toString(),
            expirationDate = binding.expirationDateEditText.text.toString(),
            cardholderName = binding.cardholderNameEditText.text.toString(),
            securityNumber = binding.securityNumberEditText.text.toString(),
        )

    private fun onTransactionResult(result: JudoPaymentResult) {
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
