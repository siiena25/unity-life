package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.converter.payload.AudioSourcePayload
import ru.mail.search.assistant.entities.audio.AudioSource

internal class AudioSourceConverter {

    fun toPayload(audioSource: AudioSource?): AudioSourcePayload? {
        return audioSource?.let {
            AudioSourcePayload(
                mediaType = audioSource.mediaType,
                uid = audioSource.uid,
                sourceType = audioSource.sourceType,
                skillName = audioSource.skillName,
                source = audioSource.source
            )
        }
    }

    fun fromPayload(payload: AudioSourcePayload?, sourceType: String? = null): AudioSource? {
        return payload?.let { audioSource ->
            AudioSource(
                mediaType = audioSource.mediaType,
                uid = audioSource.uid,
                sourceType = audioSource.sourceType ?: sourceType,
                skillName = audioSource.skillName,
                source = audioSource.source
            )
        }
    }
}