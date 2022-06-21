package ru.mail.search.assistant.media.datasource

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory
import com.google.android.exoplayer2.upstream.TransferListener

/**
 * A [Factory] that produces [L16AudioHttpDataSource] instances. straight copy of DefaultHttpDataSource
 */
internal class ElectroscopeHttpDataSourceFactory
@JvmOverloads constructor(
    private val userAgent: String,
    private val listener: TransferListener? = null,
    private val connectTimeoutMillis: Int = 2 * DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
    private val readTimeoutMillis: Int = 2 * DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
    private val allowCrossProtocolRedirects: Boolean = false
) : BaseFactory() {

    override fun createDataSourceInternal(
        defaultRequestProperties: HttpDataSource.RequestProperties
    ): L16AudioHttpDataSource {
        val dataSource = L16AudioHttpDataSource(
            userAgent,

            connectTimeoutMillis,
            readTimeoutMillis,
            allowCrossProtocolRedirects,
            defaultRequestProperties
        )
        if (listener != null) {
            dataSource.addTransferListener(listener)
        }
        return dataSource
    }
}