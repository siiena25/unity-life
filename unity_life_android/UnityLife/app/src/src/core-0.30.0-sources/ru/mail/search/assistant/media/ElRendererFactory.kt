package ru.mail.search.assistant.media

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.DefaultAudioSink
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.metadata.MetadataRenderer
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.text.TextRenderer
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.exoplayer2.video.spherical.CameraMotionRenderer
import ru.mail.search.assistant.common.util.Logger
import java.util.*

/**
 *  straight copy of DefaultRenderersFactory with overrider MediaSelector
 */

class ElRendererFactory(private val context: Context, logger: Logger?) : RenderersFactory {

    companion object {
        private const val MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50
        private const val ALLOWED_VIDEO_JOINING_TIME_MS = DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS
        private const val ENABLE_DECODER_FALLBACK = false
    }

    private val mediaCodecSelector: MediaCodecSelector = ElMediaSelector(logger)

    override fun createRenderers(
        eventHandler: Handler,
        videoRendererEventListener: VideoRendererEventListener,
        audioRendererEventListener: AudioRendererEventListener,
        textRendererOutput: TextOutput,
        metadataRendererOutput: MetadataOutput
    ): Array<Renderer> {
        val renderersList = ArrayList<Renderer>()

        renderersList.apply {
            add(
                MediaCodecVideoRenderer(
                    context,
                    mediaCodecSelector,
                    ALLOWED_VIDEO_JOINING_TIME_MS,
                    ENABLE_DECODER_FALLBACK,
                    eventHandler,
                    videoRendererEventListener,
                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
                )
            )
            add(
                MediaCodecAudioRenderer(
                    context,
                    mediaCodecSelector,
                    eventHandler,
                    audioRendererEventListener,
                    DefaultAudioSink(AudioCapabilities.getCapabilities(context), arrayOf())
                )
            )
            add(TextRenderer(textRendererOutput, eventHandler.looper))
            add(MetadataRenderer(metadataRendererOutput, eventHandler.looper))
            add(CameraMotionRenderer())
        }

        return renderersList.toTypedArray()
    }
}
