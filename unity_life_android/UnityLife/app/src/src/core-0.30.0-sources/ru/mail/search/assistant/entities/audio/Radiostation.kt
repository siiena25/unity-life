package ru.mail.search.assistant.entities.audio

data class Radiostation(
    val artist: String,
    val title: String,
    val coverUrl: String,
    val url: String,
    val audioSource: AudioSource?,
    val kwsSkipIntervals: List<KwsSkipInterval>?
)
