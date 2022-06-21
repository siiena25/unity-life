package ru.mail.search.assistant.media.datasource.mutation

import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener

open class MutatingDataSource(
    private val upstreamDataSource: DataSource,
    private val specMutation: SpecMutation?
) : DataSource {

    override fun addTransferListener(transferListener: TransferListener) {
        upstreamDataSource.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        val mutatedDataSpec = specMutation?.mutate(dataSpec) ?: dataSpec
        return upstreamDataSource.open(mutatedDataSpec)
    }

    override fun close() {
        upstreamDataSource.close()
    }

    override fun getUri(): Uri? {
        return upstreamDataSource.uri
    }

    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        return upstreamDataSource.read(buffer, offset, readLength)
    }

    override fun getResponseHeaders(): MutableMap<String, MutableList<String>> {
        return upstreamDataSource.responseHeaders
    }
}