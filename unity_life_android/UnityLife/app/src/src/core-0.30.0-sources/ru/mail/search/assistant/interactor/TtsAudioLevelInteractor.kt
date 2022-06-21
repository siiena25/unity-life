package ru.mail.search.assistant.interactor

import androidx.lifecycle.LiveData
import ru.mail.search.assistant.media.AudioLevelInterceptor

class TtsAudioLevelInteractor(private val audioLevelInterceptor: AudioLevelInterceptor) {

    val audioLevel: LiveData<Float> get() = audioLevelInterceptor.audioLevel
}