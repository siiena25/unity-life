package ru.mail.search.assistant.media.wrapper

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.media.MediaPlayer

class TtsPlayer internal constructor(private val streamPlayer: StreamPlayerAdapter) {

    val isPlaying: Boolean get() = streamPlayer.isPlaying

    fun playStream(request: HttpRequest): Flow<SoundPlayer.State> {
        val params = StreamPlayerAdapter.Params(request, MediaPlayer.Format.TTS_AUDIO)
        return streamPlayer.play(params)
    }

    fun stopPlaying() {
        streamPlayer.stop()
    }
}