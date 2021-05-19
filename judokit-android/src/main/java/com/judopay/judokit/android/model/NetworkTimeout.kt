package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.validateTimeout
import kotlinx.android.parcel.Parcelize

private const val MIN_CONNECT_TIMEOUT = 5L
private const val MAX_CONNECT_TIMEOUT = 30L
private const val MIN_READ_TIMEOUT = 180L
private const val MAX_READ_TIMEOUT = 300L
private const val MIN_WRITE_TIMEOUT = 30L
private const val MAX_WRITE_TIMEOUT = 120L

/**
 * Class for setting custom network timeout values.
 * Values must be specified in seconds.
 */
@Parcelize
class NetworkTimeout internal constructor(
    val connectTimeout: Long,
    val readTimeout: Long,
    val writeTimeout: Long
) : Parcelable {
    class Builder {
        private var connectTimeout: Long? = null
        private var readTimeout: Long? = null
        private var writeTimeout: Long? = null

        /**
         * Sets the connect timeout.
         * Defaults to 5 seconds if null.
         */
        fun setConnectTimeout(connectTimeout: Long?) =
            apply { this.connectTimeout = connectTimeout }

        /**
         * Sets the read timeout.
         * Defaults to 180 seconds if null.
         */
        fun setReadTimeout(readTimeout: Long?) = apply { this.readTimeout = readTimeout }

        /**
         * Sets the write timeout.
         * defaults to 30 seconds if null.
         */
        fun setWriteTimeout(writeTimeout: Long?) = apply { this.writeTimeout = writeTimeout }

        fun build(): NetworkTimeout {
            val myConnectTimeout =
                validateTimeout(connectTimeout, "connectTimeout", MIN_CONNECT_TIMEOUT, MAX_CONNECT_TIMEOUT)
            val myReadTimeout =
                validateTimeout(readTimeout, "readTimeout", MIN_READ_TIMEOUT, MAX_READ_TIMEOUT)
            val myWriteTimeout =
                validateTimeout(writeTimeout, "writeTimeout", MIN_WRITE_TIMEOUT, MAX_WRITE_TIMEOUT)

            return NetworkTimeout(myConnectTimeout, myReadTimeout, myWriteTimeout)
        }
    }
}
