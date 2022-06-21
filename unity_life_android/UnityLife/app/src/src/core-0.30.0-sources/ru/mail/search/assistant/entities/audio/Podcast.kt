package ru.mail.search.assistant.entities.audio

@Deprecated("Backward compatibility")
data class Podcast(
    val title: String,
    val coverUrl: String,
    val url: String,
    val audioSource: AudioSource?,
    val kwsSkipIntervals: List<KwsSkipInterval>?
)