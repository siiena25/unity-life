package ru.mail.search.assistant.entities.audio

class Tts(
    val streamId: String,
    val isBlocking: Boolean,
    val isPlayingForced: Boolean,
    val kwsSkipIntervals: List<KwsSkipInterval>?
)