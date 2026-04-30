# JudoKit-Android

[![Maven Central Version](https://img.shields.io/maven-central/v/com.judopay/judokit-android)](https://mvnrepository.com/artifact/com.judopay/judokit-android)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![GitHub Actions](https://github.com/Judopay/JudoKit-Android/actions/workflows/build-test-sdk.yml/badge.svg)](https://github.com/Judopay/JudoKit-Android/actions/workflows/build-test-sdk.yml)

An SDK to assist Android app developers to process payments on Android.

## Installation

```kotlin
dependencies {
    implementation("com.judopay:judokit-android:7.0.0")
}
```

## Usage example
Below is a simple example to showcase the basic usage of the library.
```kotlin
package com.judopay.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.JudoActivityResultContracts
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization

class MainActivity : ComponentActivity() {

    private lateinit var judoLauncher: ActivityResultLauncher<Judo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        judoLauncher = registerForActivityResult(JudoActivityResultContracts.CardPayment()) { result ->
            when (result) {
                is JudoPaymentResult.Success -> Log.i("JudoKit", "Payment succeeded: ${result.result}")
                is JudoPaymentResult.Error -> Log.e("JudoKit", "Payment error: ${result.error}")
                is JudoPaymentResult.UserCancelled -> Log.i("JudoKit", "Payment cancelled")
            }
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
                        Button(onClick = this@MainActivity::invokeJudoKit) {
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

            val judo = Judo.Builder()
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
