package com.judopay

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.NavHostFragment
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.model.navigationGraphId

class JudoActivity : AppCompatActivity() {

    private lateinit var viewModel: JudoSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.judopay_activity)

        // setup navigation graph
        val graphId = judo.paymentWidgetType.navigationGraphId
        val navigationHost = NavHostFragment.create(graphId)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, navigationHost)
                .setPrimaryNavigationFragment(navigationHost)
                .commit()

        // setup shared view-model & callbacks
        viewModel = ViewModelProvider(this).get(JudoSharedViewModel::class.java)
        viewModel.paymentResult.observe(this, Observer { dispatchPaymentResult(it) })
    }

    private fun dispatchPaymentResult(result: JudoPaymentResult) {
        setResult(result.code, result.toIntent())
        finish()
    }

}
