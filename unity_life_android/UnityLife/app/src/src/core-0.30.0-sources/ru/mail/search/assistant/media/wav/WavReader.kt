package ru.mail.search.assistant.media.wav

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.extractor.ExtractorInput
import com.google.android.exoplayer2.util.Assertions

import java.io.IOException

internal class WavReader {
    companion object {
        const val TAG = "WavHeaderReader"
        const val CHANNELS = 1
        const val SAMPLE_RATE_HZ = 24000
        const val BITS_PER_SAMPLE = 16

        @Throws(IOException::class, InterruptedException::class)
        fun peek(input: ExtractorInput): WavHeader? {
            Assertions.checkNotNull(input)
            return WavHeader(CHANNELS, SAMPLE_RATE_HZ, 48000, 2048, BITS_PER_SAMPLE, C.ENCODING_PCM_16BIT)
        }
    }
}
