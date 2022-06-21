package ru.mail.search.assistant.entities.audio

data class Tale(
    val title: String,
    val coverUrl: String,
    val url: String,
    val audioSource: AudioSource?,
    val seek: Float?,
    val kwsSkipIntervals: List<KwsSkipInterval>?
)
