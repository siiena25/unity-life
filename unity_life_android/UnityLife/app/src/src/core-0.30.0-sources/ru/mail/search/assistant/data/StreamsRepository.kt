package ru.mail.search.assistant.data

import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.media.MediaPlayer
import ru.mail.search.assistant.voiceservice.player.StreamPlayService

internal class StreamsRepository(private val streamPlayService: StreamPlayService) {

    private var streamPlayer: MediaPlayer? = null

    fun playStream(
        request: HttpRequest,
        format: MediaPlayer.Format,
        callback: (SoundPlayer.State) -> Unit
    ) {
        stopStream()
        streamPlayer = streamPlayService.playStream(request, format, callback)
    }

    fun stopStream() {
        streamPlayer?.let { player ->
            player.stop()
            player.release()
        }
        streamPlayer = null
    }

    fun isPlaying(): Boolean {
        return streamPlayer?.isPlaying == true
    }
}