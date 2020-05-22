package com.judokit.android.api.model

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import com.judokit.android.Judo
import com.judokit.android.api.error.TokenSecretError
import java.nio.charset.StandardCharsets

private const val API_SECRET_MANIFEST_NAME = "judo_api_secret"
private const val API_TOKEN_MANIFEST_NAME = "judo_api_token"

class Credentials(apiToken: String, apiSecret: String) {

    private val encodedCredentials: String = if (apiToken.isNotEmpty() && apiSecret.isNotEmpty()) {
        Base64.encodeToString("$apiToken:$apiSecret".toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    } else throw TokenSecretError("API Token & Secret is not configured correctly, either:" +
            "\t - Call to Judo.setup(\"token\", \"secret\", Judo.SANDBOX) in your Activity class" +
            "\t - Add a meta-data attributes \"judo_api_token\" and \"judo_api_secret\" to your AndroidManifest.xml file")

    val basicAuthorizationHeader: String
        get() = "Basic $encodedCredentials"

    companion object {

        @JvmStatic
        fun fromConfiguration(context: Context, judo: Judo): Credentials {
            return Credentials(getApiToken(context, judo), getApiSecret(context, judo))
        }

        private fun getApiSecret(context: Context, judo: Judo): String {
            return getManifestMetaData(context, API_SECRET_MANIFEST_NAME, judo.apiSecret)
        }

        private fun getApiToken(context: Context, judo: Judo): String {
            return getManifestMetaData(context, API_TOKEN_MANIFEST_NAME, judo.apiToken)
        }

        private fun getManifestMetaData(context: Context, attribute: String, defaultValue: String): String {
            try {
                val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val metaData = appInfo.metaData.getString(attribute)

                if (!metaData.isNullOrEmpty()) {
                    return metaData
                }
            } catch (ignore: NullPointerException) {
            } catch (ignore: PackageManager.NameNotFoundException) {
            }
            return defaultValue
        }
    }
}
