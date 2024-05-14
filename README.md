# JudoKit-Android

[![Maven Central Version](https://img.shields.io/maven-central/v/com.judopay/judokit-android)](https://mvnrepository.com/artifact/com.judopay/judokit-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/Judopay/JudoKit-Android/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/Judopay/JudoKit-Android/tree/master)

An SDK to assist Android app developers to process payments on Android.

## Installation

```kotlin
dependencies {
    implementation("com.judopay:judokit-android:4.3.2")
}
```

## Usage example
Below is a simple example to showcase the basic usage of the library.
```kotlin
package com.judopay.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.judopay.judokit.android.JUDO_ERROR
import com.judopay.judokit.android.JUDO_OPTIONS
import com.judopay.judokit.android.JUDO_RESULT
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivity
import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization

private const val GENERIC_ERROR = 0

private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
        else -> getParcelableExtra(key) as? T
    }

private class JudoKitActivityResultContract : ActivityResultContract<Judo, Pair<JudoResult?, JudoError?>>() {
    override fun createIntent(context: Context, input: Judo): Intent {
        return Intent(context, JudoActivity::class.java).apply {
            putExtra(JUDO_OPTIONS, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<JudoResult?, JudoError?> {
        val result = when (resultCode) {
            PAYMENT_SUCCESS -> Pair(intent?.parcelable<JudoResult>(JUDO_RESULT), null)
            PAYMENT_CANCELLED,
            PAYMENT_ERROR,
            -> Pair(null, intent?.parcelable<JudoError>(JUDO_ERROR))
            else -> null
        }

        if (result == null || result.first == null && result.second == null) {
            return Pair(null, JudoError(GENERIC_ERROR, "Unknown error"))
        }

        return result
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var judoLauncher: ActivityResultLauncher<Judo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        judoLauncher = registerForActivityResult(JudoKitActivityResultContract()) { result ->
            // Handle the result here
            Log.i("JudoKit", "Result: $result")
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = this@MainActivity::invokeJudoKit,
                        ) {
                            Text("Invoke JudoKit")
                        }
                    }
                }
            }
        }
    }

    private fun invokeJudoKit() {
        try {
            val amount = Amount.Builder()
                .setCurrency(Currency.GBP)
                .setAmount("0.01")
                .build()

            val reference = Reference.Builder()
                .setConsumerReference("my-consumer-reference")
                .setPaymentReference("my-payment-reference")
                .build()

            val authorization = PaymentSessionAuthorization.Builder()
                .setPaymentSession("payment-session")
                .setApiToken("token")
                .build()

            val judo = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
                .setJudoId("123456")
                .setAmount(amount)
                .setReference(reference)
                .setAuthorization(authorization)
                .build()

            judoLauncher.launch(judo)

        } catch (e: Exception) {
            Log.e("JudoKit", "Error invoking JudoKit", e)
        }
    }
}

```

## See also

- [JudoKit-Android documentation](https://docs.judopay.com/Content/Mobile/Android.htm)
- [Judopay Transaction API documentation](https://docs.judopay.com/api-reference/index.html)

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/Judopay/JudoKit-Android/blob/master/LICENSE) file for details.
