package ru.mail.search.assistant.media.wav

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.extractor.SeekMap
import com.google.android.exoplayer2.extractor.SeekPoint
import com.google.android.exoplayer2.util.Util

internal class WavHeader(val numChannels: Int,
                val sampleRateHz: Int,
                private val averageBytesPerSecond: Int,
                val bytesPerFrame: Int,
                private val bitsPerSample: Int,
                @param:C.PcmEncoding @field:C.PcmEncoding
                @get:C.PcmEncoding
                val encoding: Int) : SeekMap {

    private var dataStartPosition: Long = 0
    private var dataSize: Long = 0

    val bitrate: Int
        get() = sampleRateHz * bitsPerSample * numChannels

    fun hasDataBounds(): Boolean {
        return dataStartPosition != 0L && dataSize != 0L
    }

    // SeekMap implementation.

    override fun isSeekable(): Boolean {
        return false
    }

    override fun getDurationUs(): Long {
        val numFrames = dataSize / bytesPerFrame
        return numFrames * C.MICROS_PER_SECOND / sampleRateHz
    }

    override fun getSeekPoints(timeUs: Long): SeekMap.SeekPoints {
        var positionOffset = timeUs * averageBytesPerSecond / C.MICROS_PER_SECOND
        // Constrain to nearest preceding frame offset.
        positionOffset = positionOffset / bytesPerFrame * bytesPerFrame
        positionOffset = Util.constrainValue(positionOffset, 0, dataSize - bytesPerFrame)
        val seekPosition = dataStartPosition + positionOffset
        val seekTimeUs = getTimeUs(seekPosition)
        val seekPoint = SeekPoint(seekTimeUs, seekPosition)
        if (seekTimeUs >= timeUs || positionOffset == dataSize - bytesPerFrame) {
            return SeekMap.SeekPoints(seekPoint)
        } else {
            val secondSeekPosition = seekPosition + bytesPerFrame
            val secondSeekTimeUs = getTimeUs(secondSeekPosition)
            val secondSeekPoint = SeekPoint(secondSeekTimeUs, secondSeekPosition)
            return SeekMap.SeekPoints(seekPoint, secondSeekPoint)
        }
    }

    fun getTimeUs(position: Long): Long {
        val positionOffset = Math.max(0, position - dataStartPosition)
        return positionOffset * C.MICROS_PER_SECOND / averageBytesPerSecond
    }

}