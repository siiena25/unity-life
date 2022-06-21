package ru.mail.search.assistant.voiceservice.player

import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.media.MediaPlayer
import ru.mail.search.assistant.media.MediaPlayerFactory

internal class StreamPlayService(private val mediaPlayerFactory: MediaPlayerFactory) {

    fun playStream(
        request: HttpRequest,
        format: MediaPlayer.Format,
        event: (SoundPlayer.State) -> Unit
    ): MediaPlayer {
        return MediaPlayer(mediaPlayerFactory).apply {
            val listener = object : MediaPlayer.PlayerEventListener {

                override fun onPlaying(eventTime: Long, position: Long) {
                    event(SoundPlayer.State.Playing(eventTime, position))
                }

                override fun onLoading() {
                    event(SoundPlayer.State.Paused)
                }

                override fun onFinish() {
                    event(SoundPlayer.State.Finished)
                }
            }
            start(format, request, listener)
        }
    }
}