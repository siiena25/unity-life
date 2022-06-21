package ru.mail.search.assistant.media

import android.content.Context
import android.os.SystemClock
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.EventLogger
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.media.datasource.ElectroscopeHttpDataSourceFactory
import ru.mail.search.assistant.media.wav.AssistantExtractorsFactory
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError

internal class MediaPlayerFactory(
    private val context: Context,
    private val analytics: Analytics?,
    private val audioLevelInterceptor: AudioLevelInterceptor,
    private val bandwidthMeter: AssistantBandwidthMeter,
    private val logger: Logger?
) {

    companion object {
        internal const val DEFAULT_DATA_SOURCE_TAG = "exo"
        private const val L16A_DATA_SOURCE = "l16exo"
        private const val TAG = "MediaPlayerFactory"
    }

    fun createPlayer(
        request: HttpRequest,
        listener: MediaPlayer.PlayerEventListener,
        format: MediaPlayer.Format?
    ): ExoPlayer {
        return createPlayer(format, listener, provideRendererFactory(format)).apply {
            val source = if (format == MediaPlayer.Format.TTS_AUDIO) {
                createL16AudioSource(request)
            } else {
                createMediaSource(request)
            }
            setMediaSource(source)
            prepare()
        }
    }

    private fun createPlayer(
        format: MediaPlayer.Format?,
        listener: MediaPlayer.PlayerEventListener,
        rendererFactory: RenderersFactory
    ): ExoPlayer {
        val trackSelector = DefaultTrackSelector(context)
        val loadControl = AssistantLoadControl()

        val analyticsCollector = AnalyticsCollector(Clock.DEFAULT)
        analyticsCollector.addListener(EventLogger(trackSelector))

        val player = SimpleExoPlayer.Builder(context, rendererFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setAnalyticsCollector(analyticsCollector)
            .setBandwidthMeter(bandwidthMeter)
            .build().apply {
                playWhenReady = true
                addListener(PlayerEventListener(this, listener, analytics, logger))
            }

        return when (format) {
            MediaPlayer.Format.TTS_AUDIO -> TTSPlayer(player, audioLevelInterceptor)
            else -> player
        }
    }

    private fun provideRendererFactory(format: MediaPlayer.Format? = null): RenderersFactory {
        return if (format == MediaPlayer.Format.TTS_AUDIO) {
            ElRendererFactory(context, logger)
        } else {
            DefaultRenderersFactory(context)
        }
    }

    private fun createMediaSource(request: HttpRequest): MediaSource {
        return buildDefaultMediaSourceFactory(request.headers)
            .createMediaSource(MediaItem.fromUri(request.url))
    }

    private fun createL16AudioSource(request: HttpRequest): MediaSource {
        return buildL16MediaSourceFactory(request.headers)
            .createMediaSource(MediaItem.fromUri(request.url))
    }

    private fun buildDefaultMediaSourceFactory(headers: Map<String, String>): ProgressiveMediaSource.Factory {
        val httpDsf = DefaultHttpDataSource.Factory()
            .setUserAgent(DEFAULT_DATA_SOURCE_TAG)
            .setDefaultRequestProperties(headers)
        return ProgressiveMediaSource.Factory(httpDsf)
    }

    private fun buildL16MediaSourceFactory(headers: Map<String, String>): ProgressiveMediaSource.Factory {
        val httpDsf = ElectroscopeHttpDataSourceFactory(L16A_DATA_SOURCE)
        httpDsf.setDefaultRequestProperties(headers)
        val extractorsFactory = AssistantExtractorsFactory()
        return ProgressiveMediaSource.Factory(httpDsf, extractorsFactory)
    }

    private class PlayerEventListener(
        private val player: ExoPlayer,
        private val listener: MediaPlayer.PlayerEventListener,
        private val analytics: Analytics?,
        private val logger: Logger?
    ) : Player.Listener {

        private var isPlayingStarted = false

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                    if (!playWhenReady || isPlayingStarted) {
                        listener.onFinish()
                    }
                }
                Player.STATE_BUFFERING -> {
                    isPlayingStarted = true
                    listener.onLoading()
                }
                Player.STATE_READY -> {
                    if (playWhenReady) {
                        isPlayingStarted = true
                        listener.onPlaying(SystemClock.elapsedRealtime(), player.currentPosition)
                    } else {
                        listener.onFinish()
                    }
                }
                Player.STATE_ENDED -> {
                    listener.onFinish()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)

            val errorMessage = if (error is ExoPlaybackException) {
                getErrorMessage(error)
            } else {
                null
            }
            logger?.e(TAG, error)
            analytics?.log(MediaPlayerError(errorMessage))
        }

        private fun getErrorMessage(error: ExoPlaybackException): String? {
            return when (error.type) {
                ExoPlaybackException.TYPE_SOURCE -> error.sourceException.javaClass.simpleName
                ExoPlaybackException.TYPE_RENDERER -> error.rendererException.javaClass.simpleName
                ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.javaClass.simpleName
                else -> null
            }
        }
    }
}