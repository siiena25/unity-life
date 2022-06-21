package ru.mail.search.assistant.media.datasource

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

internal class L16AudioHttpDataSource(
    userAgent: String,
    connectTimeoutMillis: Int,
    readTimeoutMillis: Int,
    allowCrossProtocolRedirects: Boolean,
    defaultRequestProperties: HttpDataSource.RequestProperties?
) : DefaultHttpDataSource(
    userAgent,
    connectTimeoutMillis,
    readTimeoutMillis,
    allowCrossProtocolRedirects,
    defaultRequestProperties
) {

    private val cutoffByte = AtomicReference<Byte>()
    private val cutoffFlag = AtomicBoolean(false)

    @Throws(HttpDataSource.HttpDataSourceException::class)
    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {

        val extraBuff = ByteArray(readLength)

        val count = super.read(extraBuff, 0, readLength)
        var resultCount: Int = count

        if (count > 0) {
            if (cutoffFlag.get()) {
                buffer[offset] = extraBuff[0]
                buffer[offset + 1] = cutoffByte.get()
                resultCount = count + 1

                if (resultCount.rem(2) != 0) {
                    cutoffByte.set(extraBuff[count - 1])
                    be2le(
                        extraBuff.copyOfRange(
                            1,
                            count - 1
                        )
                    ).forEachIndexed { i, byte -> buffer[i + offset + 2] = byte }
                    resultCount = count
                } else {
                    cutoffFlag.set(false)
                    be2le(
                        extraBuff.copyOfRange(
                            1,
                            count
                        )
                    ).forEachIndexed { i, byte -> buffer[i + offset + 2] = byte }
                }
            } else {
                if (count.rem(2) != 0) {
                    cutoffFlag.set(true)
                    cutoffByte.set(extraBuff[count - 1])
                    be2le(
                        extraBuff.copyOfRange(
                            0,
                            count - 1
                        )
                    ).forEachIndexed { i, byte -> buffer[i + offset] = byte }
                    resultCount = count - 1
                } else {
                    resultCount = count
                    be2le(extraBuff).forEachIndexed { i, byte -> buffer[i + offset] = byte }
                }
            }
        }
        return resultCount
    }

    private fun be2le(input: ByteArray): ByteArray {
        val sb = ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val sa = ShortArray(sb.limit())
        sb.get(sa)
        val nbf = ByteBuffer.allocate(sa.size * 2)
        sa.forEach { nbf.putShort(it) }
        return nbf.array()
    }
}
