package ru.mail.search.assistant.media

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.upstream.*
import ru.mail.search.assistant.AssistantMediaTransferListener

internal class AssistantBandwidthMeter(
    context: Context,
) : BandwidthMeter, TransferListener {

    private var externalListener: AssistantMediaTransferListener? = null
    private val defaultBandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(context)

    fun setTransferListener(externalListener: AssistantMediaTransferListener?) {
        this.externalListener = externalListener
    }

    fun removeTransferListener() {
        externalListener = null
    }

    override fun getBitrateEstimate(): Long {
        return defaultBandwidthMeter.bitrateEstimate
    }

    override fun getTransferListener(): TransferListener {
        return this
    }

    override fun addEventListener(
        eventHandler: Handler,
        eventListener: BandwidthMeter.EventListener
    ) {
        defaultBandwidthMeter.addEventListener(eventHandler, eventListener)
    }

    override fun removeEventListener(eventListener: BandwidthMeter.EventListener) {
        defaultBandwidthMeter.removeEventListener(eventListener)
    }

    override fun onTransferInitializing(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        externalListener?.onTransferInitializing(source.uri?.truncate(), isNetwork)
        defaultBandwidthMeter.onTransferInitializing(source, dataSpec, isNetwork)
    }

    override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        externalListener?.onTransferStart(source.uri?.truncate(), isNetwork)
        defaultBandwidthMeter.onTransferStart(source, dataSpec, isNetwork)
    }

    override fun onBytesTransferred(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean,
        bytesTransferred: Int
    ) {
        externalListener?.onBytesTransferred(source.uri?.truncate(), isNetwork, bytesTransferred)
        defaultBandwidthMeter.onBytesTransferred(source, dataSpec, isNetwork, bytesTransferred)
    }

    override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        externalListener?.onTransferEnd(source.uri?.truncate(), isNetwork)
        defaultBandwidthMeter.onTransferEnd(source, dataSpec, isNetwork)
    }

    // Only host and first path segment is allowed, no query params because it can leak
    // device id and etc.
    private fun Uri.truncate(): Uri? {
        val builder = Uri.Builder()
            .scheme(scheme)
            .authority(authority)
        val path = pathSegments.firstOrNull()
        path?.let(builder::appendPath)
        return builder.build().normalizeScheme()
    }
}