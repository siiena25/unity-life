package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.converter.payload.KwsSkipIntervalPayload
import ru.mail.search.assistant.entities.audio.KwsSkipInterval

internal class KwsSkipIntervalsConverter {

    fun toPayload(intervals: List<KwsSkipInterval>?): List<KwsSkipIntervalPayload>? {
        return intervals?.map { interval -> KwsSkipIntervalPayload(interval.start, interval.end) }
    }

    fun fromPayload(intervals: List<KwsSkipIntervalPayload>?): List<KwsSkipInterval>? {
        return intervals?.map { interval -> KwsSkipInterval(interval.start, interval.end) }
    }
}