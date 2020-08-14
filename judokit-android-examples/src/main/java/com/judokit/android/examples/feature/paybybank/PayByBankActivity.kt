package com.judokit.android.examples.feature.paybybank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.judokit.android.JUDO_OPTIONS
import com.judokit.android.Judo
import com.judokit.android.JudoActivity
import com.judokit.android.examples.R
import com.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_pay_by_bank.*

class PayByBankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_by_bank)

        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)

        payByBankButton.setOnClickListener {
            val intent = Intent(this, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, judo)
            startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        payByBankButton.isEnabled = true
        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            setResult(resultCode, data)
            finish()
        }
    }
}
