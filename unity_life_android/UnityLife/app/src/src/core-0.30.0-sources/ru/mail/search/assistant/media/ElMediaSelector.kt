package ru.mail.search.assistant.media

import android.os.Build
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import ru.mail.search.assistant.common.util.Logger

internal class ElMediaSelector(private val logger: Logger?) : MediaCodecSelector {

    companion object {
        private const val MIME_TYPE_RAW = "audio/raw"
        private const val DECODER_SOFT_RAW_GOOGLE = "OMX.google.raw.decoder"
        private const val TAG = "ElMediaSelector"
    }

    override fun getDecoderInfos(
        mimeType: String,
        requiresSecureDecoder: Boolean,
        requiresTunnelingDecoder: Boolean
    ): MutableList<MediaCodecInfo> {
        val supportedDecoders = MediaCodecUtil.getDecoderInfos(
            mimeType,
            requiresSecureDecoder,
            requiresTunnelingDecoder
        )
        return if (mimeType == MIME_TYPE_RAW && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val googleSoftDecoder =
                MediaCodecInfo.newInstance(
                    DECODER_SOFT_RAW_GOOGLE,
                    MIME_TYPE_RAW,
                    MIME_TYPE_RAW,
                    null,
                    false,
                    true,
                    false,
                    false,
                    false
                )
            val mutable = mutableListOf<MediaCodecInfo>()
            mutable.apply {
                add(googleSoftDecoder)
                addAll(supportedDecoders)
            }
            logger?.d(
                TAG,
                "added google software codec, available codecs are = ${mutable.joinToString { it.name }}"
            )
            mutable
        } else
            supportedDecoders
    }
}