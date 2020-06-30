package com.judokit.android.api.model

import android.os.Parcelable
import okhttp3.Headers

interface Authorization : Parcelable {
    val headers: Headers
}
