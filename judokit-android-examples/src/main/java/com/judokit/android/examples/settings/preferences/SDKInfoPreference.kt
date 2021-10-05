package com.judokit.android.examples.settings.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.judopay.judokit.android.ui.common.JUDO_API_VERSION
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION

class SDKInfoPreference : Preference {
    constructor(context: Context?) : super(context) { commonInit() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { commonInit() }
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { commonInit() }

    private fun commonInit() {
        setSummaryProvider { preference ->
            when (preference.key) {
                "sdk_version" -> JUDO_KIT_VERSION
                "judo_api_version" -> JUDO_API_VERSION
                else -> "Invalid preference key provided"
            }
        }
    }
}
