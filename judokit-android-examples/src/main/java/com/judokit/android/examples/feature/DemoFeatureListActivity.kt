package com.judokit.android.examples.feature

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.judokit.android.examples.R
import com.judokit.android.examples.common.NotificationPermissionLauncher
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.databinding.ActivityDemoFeatureListBinding
import com.judokit.android.examples.databinding.DialogGetTransactionBinding
import com.judokit.android.examples.feature.adapter.DemoFeaturesAdapter
import com.judokit.android.examples.feature.noui.DemoNoUiPaymentActivity
import com.judokit.android.examples.feature.tokenpayments.TokenPaymentsActivity
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.examples.settings.SettingsActivity
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JudoActivityResultContracts
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.factory.RecommendationApiServiceFactory
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import kotlinx.coroutines.launch

const val CHUCKER_MAX_CONTENT_LENGTH = 250000L

class DemoFeatureListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoFeatureListBinding
    private lateinit var viewModel: DemoFeatureListViewModel
    private lateinit var notificationPermissionLauncher: NotificationPermissionLauncher

    private var deepLinkIntent = intent
    private var getTransactionDialog: AlertDialog? = null

    private val judoLaunchers =
        mapOf(
            PaymentWidgetType.CARD_PAYMENT to
                registerForActivityResult(JudoActivityResultContracts.CardPayment()) { onJudoResult(it) },
            PaymentWidgetType.PRE_AUTH to
                registerForActivityResult(JudoActivityResultContracts.CardPreAuth()) { onJudoResult(it) },
            PaymentWidgetType.CREATE_CARD_TOKEN to
                registerForActivityResult(JudoActivityResultContracts.CreateCardToken()) { onJudoResult(it) },
            PaymentWidgetType.CHECK_CARD to
                registerForActivityResult(JudoActivityResultContracts.CheckCard()) { onJudoResult(it) },
            PaymentWidgetType.GOOGLE_PAY to
                registerForActivityResult(JudoActivityResultContracts.GooglePay()) { onJudoResult(it) },
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY to
                registerForActivityResult(JudoActivityResultContracts.PreAuthGooglePay()) { onJudoResult(it) },
            PaymentWidgetType.PAYMENT_METHODS to
                registerForActivityResult(JudoActivityResultContracts.PaymentMethods()) { onJudoResult(it) },
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS to
                registerForActivityResult(JudoActivityResultContracts.PreAuthPaymentMethods()) { onJudoResult(it) },
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS to
                registerForActivityResult(JudoActivityResultContracts.ServerToServerPaymentMethods()) { onJudoResult(it) },
        )

    private val otherPaymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            onJudoResult(
                JudoActivityResultContracts.CardPayment().parseResult(activityResult.resultCode, activityResult.data),
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        notificationPermissionLauncher = NotificationPermissionLauncher(this)

        setupChucker()

        binding = ActivityDemoFeatureListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        viewModel = ViewModelProvider(this)[DemoFeatureListViewModel::class.java]

        setupRecyclerView()

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        notificationPermissionLauncher.requestPermissionIfNeeded()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { handleEffect(it) }
            }
        }
    }

    private fun setupChucker() {
        val interceptors =
            listOf(
                ChuckerInterceptor
                    .Builder(this)
                    .collector(ChuckerCollector(this))
                    .maxContentLength(CHUCKER_MAX_CONTENT_LENGTH)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build(),
            )
        JudoApiServiceFactory.externalInterceptors = interceptors
        RecommendationApiServiceFactory.externalInterceptors = interceptors
    }

    private fun setupRecyclerView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsets.bottom)
            insets
        }
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(this@DemoFeatureListActivity, DividerItemDecoration.VERTICAL))
            adapter = DemoFeaturesAdapter(DemoFeature.entries) { viewModel.onFeatureSelected(it) }
        }
    }

    private fun onJudoResult(result: JudoPaymentResult) {
        deepLinkIntent = intent
        viewModel.handleJudoPaymentResult(result)
    }

    private fun handleEffect(effect: DemoFeatureListEffect) {
        when (effect) {
            is DemoFeatureListEffect.LaunchJudoWidget ->
                judoLaunchers[effect.judo.paymentWidgetType]?.launch(effect.judo)

            is DemoFeatureListEffect.LaunchNoUiPayment ->
                otherPaymentLauncher.launch(
                    Intent(this, DemoNoUiPaymentActivity::class.java).apply {
                        putExtra(JUDO_OPTIONS, effect.judo)
                    },
                )

            is DemoFeatureListEffect.LaunchTokenPayments ->
                otherPaymentLauncher.launch(
                    Intent(this, TokenPaymentsActivity::class.java).apply {
                        putExtra(JUDO_OPTIONS, effect.judo)
                    },
                )

            is DemoFeatureListEffect.ShowGetTransactionDialog -> showGetTransactionDialog()

            is DemoFeatureListEffect.ShowResult -> {
                dismissGetTransactionDialog()
                startResultActivity(effect.result)
            }

            is DemoFeatureListEffect.ShowError -> {
                dismissGetTransactionDialog()
                presentError(effect.message)
            }

            is DemoFeatureListEffect.ShowUserCancelled -> {
                dismissGetTransactionDialog()
                toast("User cancelled the payment.")
            }
        }
    }

    private fun showGetTransactionDialog() {
        val dialogBinding = DialogGetTransactionBinding.inflate(layoutInflater)

        val dialog =
            AlertDialog
                .Builder(this)
                .setTitle(R.string.feature_title_get_transaction_details)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.dialog_button_ok, null) // null prevents auto-dismiss
                .setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
                .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogBinding.receiptProgressBar.visibility = View.VISIBLE
                dialogBinding.receiptIdEditText.visibility = View.GONE
                viewModel.fetchTransactionDetails(dialogBinding.receiptIdEditText.text.toString())
            }
        }

        getTransactionDialog = dialog
        dialog.show()
    }

    private fun dismissGetTransactionDialog() {
        getTransactionDialog?.dismiss()
        getTransactionDialog = null
    }

    private fun presentError(message: String) {
        if (isFinishing || isDestroyed) return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_error_title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_button_ok, null)
            .show()
    }

    private fun toast(message: String) = Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG).show()
}
