package com.judopay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.judopay.ui.paymentmethods.PaymentMethodsFragment

class JudoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.judopay_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PaymentMethodsFragment())
                    .commitNow()
        }
    }

}
