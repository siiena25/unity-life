package ru.mail.search.assistant.interactor

import androidx.annotation.MainThread
import ru.mail.search.assistant.media.wrapper.StreamPlayerAdapter
import ru.mail.search.assistant.media.wrapper.TtsPlayer

class TtsInteractor internal constructor(
    ttsAudioLevel: TtsAudioLevelInteractor,
    private val ttsPlayer: TtsPlayer,
    private val streamPlayerAdapter: StreamPlayerAdapter
) {

    val audioLevel = ttsAudioLevel.audioLevel

    fun isPlaying(): Boolean {
        return ttsPlayer.isPlaying || streamPlayerAdapter.isPlaying
    }

    @MainThread
    fun stop() {
        ttsPlayer.stopPlaying()
    }
}