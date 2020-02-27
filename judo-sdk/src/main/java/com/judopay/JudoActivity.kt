package com.judopay

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.judopay.api.model.response.Receipt
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

    fun sendResult(resultCode: Int, receipt: Receipt) {
        val intent = Intent()
        intent.putExtra(JUDO_RECEIPT, receipt)
        setResult(resultCode, intent)
        if (resultCode == RESULT_ERROR) {
            vibrate(this)
            showAlertDialog()
        } else {
            finish()
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.transaction_unsuccessful)
            .setNegativeButton(R.string.close) { _, _ -> finish() }
            .show()
    }

    private fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    150L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(150L)
        }
    }
}
