package com.judokit.android.examples.settings.fragments

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import com.judokit.android.examples.R
import com.judokit.android.examples.apiclient.ApiClient
import com.judokit.android.examples.apiclient.CreatePaymentSessionRequest
import com.judokit.android.examples.apiclient.CreatePaymentSessionResponse
import com.judopay.judokit.android.model.ApiEnvironment
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.common.ProgressButton
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
import java.util.UUID

const val CHUCKER_MAX_CONTENT_LENGTH = 250000L

class CustomPreference
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0,
    ) : Preference(context, attrs, defStyleAttr) {
        var onGeneratePaymentSessionClickListener: View.OnClickListener? = null

        override fun onBindViewHolder(holder: PreferenceViewHolder) {
            super.onBindViewHolder(holder)
            with(holder.itemView) {
                findViewById<ProgressButton>(R.id.generatePaymentSessionButton)
                    .setOnClickListener(onGeneratePaymentSessionClickListener)
            }
        }
    }

class RootFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preference = findPreference<CustomPreference>("generate_payment_session")!!

        preference.onGeneratePaymentSessionClickListener =
            View.OnClickListener {
                createPaymentSession(it as ProgressButton)
            }
    }

    private fun createPaymentSession(progressButton: ProgressButton) {
        progressButton.state = ButtonState.Loading

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        val randomReference = UUID.randomUUID().toString()
        sharedPreferences.edit().putString("payment_reference", randomReference).apply()

        createApiClient().createPaymentSession(
            CreatePaymentSessionRequest(
                judoId = sharedPreferences.getString("judo_id", "") ?: "",
                amount = sharedPreferences.getString("amount", "") ?: "",
                currency = sharedPreferences.getString("currency", "") ?: "",
                yourPaymentReference = sharedPreferences.getString("payment_reference", "") ?: "",
                yourConsumerReference = "my-unique-ref",
            ),
        ).enqueue(
            object : Callback<CreatePaymentSessionResponse> {
                override fun onFailure(
                    call: Call<CreatePaymentSessionResponse>,
                    t: Throwable,
                ) {
                    progressButton.state = ButtonState.Enabled(R.string.generate_payment_session_title)
                    updatePaymentSession(null, t)
                }

                override fun onResponse(
                    call: Call<CreatePaymentSessionResponse>,
                    response: Response<CreatePaymentSessionResponse>,
                ) {
                    progressButton.state = ButtonState.Enabled(R.string.generate_payment_session_title)
                    updatePaymentSession(response.body()?.reference)
                }
            },
        )
    }

    private fun updatePaymentSession(
        session: String?,
        error: Throwable? = null,
    ) {
        if (session.isNullOrBlank()) {
            Toast.makeText(activity, error?.localizedMessage ?: "Failed to generate payment session.", Toast.LENGTH_LONG).show()
            return
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        sharedPreferences.edit().putString("payment_session", session).apply()

        Toast.makeText(activity, "Payment session created!", Toast.LENGTH_LONG).show()
        requireActivity().finish()
    }

    private fun createApiClient(): ApiClient {
        val activity = requireActivity()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        val baseUrl =
            if (sharedPreferences.getBoolean("is_sandboxed", false)) {
                ApiEnvironment.SANDBOX.host
            } else {
                ApiEnvironment.LIVE.host
            }

        val apiToken = sharedPreferences.getString("token", "") ?: ""
        val apiSecret = sharedPreferences.getString("secret", "") ?: ""

        val encodedCredentials =
            Base64.encodeToString(
                "$apiToken:$apiSecret".toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP,
            )

        val gson = GsonBuilder().create()

        val chuck =
            ChuckerInterceptor.Builder(activity)
                .collector(ChuckerCollector(activity))
                .maxContentLength(CHUCKER_MAX_CONTENT_LENGTH)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build()

        val client =
            OkHttpClient.Builder()
                .addInterceptor(chuck)
                .addInterceptor { chain ->
                    val original = chain.request()

                    val request =
                        original.newBuilder()
                            .header("User-Agent", "JudoKit-Android Examples")
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Api-Version", "6.20.0")
                            .header("Cache-Control", "no-cache")
                            .header("Authorization", "Basic $encodedCredentials")
                            .build()

                    chain.proceed(request)
                }
                .build()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(ApiClient::class.java)
    }
}
