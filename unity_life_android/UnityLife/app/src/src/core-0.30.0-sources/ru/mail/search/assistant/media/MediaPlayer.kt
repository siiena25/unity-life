package ru.mail.search.assistant.media

import com.google.android.exoplayer2.ExoPlayer
import ru.mail.search.assistant.common.http.common.HttpRequest

internal class MediaPlayer(private val mediaPlayerFactory: MediaPlayerFactory) {

    private var player: ExoPlayer? = null
    private var listener: PlayerEventListener? = null

    var isPlaying: Boolean = false
        private set

    fun start(
        format: Format,
        request: HttpRequest,
        eventListener: PlayerEventListener
    ) {
        startPlayer(request, eventListener, format)
    }

    fun stop() {
        player?.apply {
            playWhenReady = false
            stop()
        }
    }

    fun release() {
        player?.release()
        player = null
        listener = null
    }

    private fun startPlayer(
        request: HttpRequest,
        eventListener: PlayerEventListener,
        format: Format?
    ) {
        val listener = createMappedEventListener(eventListener)
        setupPlayer(mediaPlayerFactory.createPlayer(request, listener, format), listener)
    }

    private fun setupPlayer(player: ExoPlayer, listener: PlayerEventListener) {
        release()
        this.player = player
        this.listener = listener
    }

    private fun createMappedEventListener(eventListener: PlayerEventListener): PlayerEventListener {
        return object : PlayerEventListener by eventListener {

            override fun onLoading() {
                isPlaying = true
                eventListener.onLoading()
            }

            override fun onPlaying(eventTime: Long, position: Long) {
                isPlaying = true
                eventListener.onPlaying(eventTime, position)
            }

            override fun onFinish() {
                isPlaying = false
                eventListener.onFinish()
            }
        }
    }

    interface PlayerEventListener {

        fun onLoading()
        fun onPlaying(eventTime: Long, position: Long)
        fun onFinish()
    }

    enum class Format {

        DEFAULT, TTS_AUDIO
    }
}