package ru.mail.search.assistant.media.wav

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.ParserException
import com.google.android.exoplayer2.extractor.*
import com.google.android.exoplayer2.util.MimeTypes
import java.io.IOException

internal class WavExtractor : Extractor {
    private lateinit var extractorOutput: ExtractorOutput
    private lateinit var trackOutput: TrackOutput
    private var wavHeader: WavHeader? = null
    private var bytesPerFrame: Int = 0
    private var pendingBytes: Int = 0

    @Throws(IOException::class, InterruptedException::class)
    override fun sniff(input: ExtractorInput) = WavReader.peek(input) != null

    override fun init(output: ExtractorOutput) {
        extractorOutput = output
        trackOutput = output.track(0, C.TRACK_TYPE_AUDIO)
        wavHeader = null
        output.endTracks()
    }

    override fun seek(position: Long, timeUs: Long) {
        pendingBytes = 0
    }

    override fun release() {
        // Do nothing
    }

    @Throws(IOException::class, InterruptedException::class)
    override fun read(input: ExtractorInput, seekPosition: PositionHolder): Int {
        if (wavHeader == null) {
            wavHeader = WavReader.peek(input)
            if (wavHeader == null) {
                //        Should only happen if the media wasn't sniffed.
                throw ParserException.createForMalformedContainer("Unsupported or unrecognized wav header.", null)
            }
        }

        val header = wavHeader

        header?.let {
            val format = Format.Builder()
                .setSampleMimeType(MimeTypes.AUDIO_RAW)
                .setAverageBitrate(header.bitrate)
                .setPeakBitrate(header.bitrate)
                .setMaxInputSize(MAX_INPUT_SIZE)
                .setChannelCount(header.numChannels)
                .setSampleRate(header.sampleRateHz)
                .setPcmEncoding(header.encoding)
                .setSelectionFlags(0)
                .build()

            trackOutput.format(format)
            bytesPerFrame = header.bytesPerFrame

            if (!header.hasDataBounds()) {
                extractorOutput.seekMap(header)
            }

            val bytesLeft: Long = 8 * 1024
            val maxBytesToRead =
                Math.min((MAX_INPUT_SIZE - pendingBytes).toLong(), bytesLeft).toInt()
            val bytesAppended = trackOutput.sampleData(input, maxBytesToRead, true)
            if (bytesAppended != Extractor.RESULT_END_OF_INPUT) {
                pendingBytes += bytesAppended
            }

            // Samples must consist of a whole number of frames.
            val pendingFrames = pendingBytes / bytesPerFrame
            if (pendingFrames > 0) {
                val timeUs = header.getTimeUs(input.position - pendingBytes)
                val size = pendingFrames * bytesPerFrame
                pendingBytes -= size
                trackOutput.sampleMetadata(
                    timeUs,
                    C.BUFFER_FLAG_KEY_FRAME,
                    size,
                    pendingBytes,
                    null
                )
            }

            return if (bytesAppended == Extractor.RESULT_END_OF_INPUT) Extractor.RESULT_END_OF_INPUT else Extractor.RESULT_CONTINUE
        }
        return Extractor.RESULT_END_OF_INPUT
    }

    companion object {

        val FACTORY = { arrayOf<Extractor>(WavExtractor()) }

        /**
         * Arbitrary maximum input size of 32KB, which is ~170ms of 16-bit stereo PCM audio at 48KHz.
         */
        const val MAX_INPUT_SIZE = 32 * 1024
    }
}