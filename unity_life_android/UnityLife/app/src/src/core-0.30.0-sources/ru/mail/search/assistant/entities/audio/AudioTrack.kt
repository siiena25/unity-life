package ru.mail.search.assistant.entities.audio

data class AudioTrack(
    val id: Long,
    val artistName: String,
    val trackName: String,
    val coverUrl: String,
    val url: String,
    val audioSource: AudioSource?,
    val kwsSkipIntervals: List<KwsSkipInterval>?,
    val duration: Long = 0,
    val isHq: Boolean = false,
    val playbackLimit: PlaybackLimit? = null,
    val seek: Float?,
    val statFlags: String? = null
)
