package ru.mail.search.assistant.entities.audio

data class KwsSkipInterval(
    val start: Long,
    val end: Long
) {

    companion object {

        const val INTERVAL_UNLIMITED = -1L
    }

    fun isUnlimited(): Boolean {
        return end == INTERVAL_UNLIMITED
    }
}