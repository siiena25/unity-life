package ru.mail.search.assistant.entities.audio

data class Sound(
    val name: String,
    val url: String,
    val audioSource: AudioSource?,
    val kwsSkipIntervals: List<KwsSkipInterval>?
)
