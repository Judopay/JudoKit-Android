package com.judokit.android.examples.feature.paybybank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.judokit.android.examples.databinding.ActivityPayByBankBinding
import com.judokit.android.examples.feature.JUDO_PAYMENT_WIDGET_REQUEST_CODE
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity

class PayByBankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayByBankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayByBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val judo = intent.getParcelableExtra<Judo>(JUDO_OPTIONS)

        binding.payByBankButton.setOnClickListener {
            val intent = Intent(this, JudoActivity::class.java)
            intent.putExtra(JUDO_OPTIONS, judo)
            startActivityForResult(intent, JUDO_PAYMENT_WIDGET_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.payByBankButton.isEnabled = true
        if (requestCode == JUDO_PAYMENT_WIDGET_REQUEST_CODE) {
            setResult(resultCode, data)
            finish()
        }
    }
}
