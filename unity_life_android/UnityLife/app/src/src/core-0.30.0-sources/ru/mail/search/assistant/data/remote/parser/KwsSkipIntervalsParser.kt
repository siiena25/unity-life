package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.util.getArray
import ru.mail.search.assistant.common.util.toArray
import ru.mail.search.assistant.common.util.toFloat
import ru.mail.search.assistant.entities.audio.KwsSkipInterval

class KwsSkipIntervalsParser {

    private companion object {

        private const val SECOND_MS = 1_000L
    }

    fun parse(json: JsonObject): List<KwsSkipInterval>? {
        return json.getArray("kws_skip")
            ?.mapNotNull { intervalJson ->
                intervalJson.toArray()
                    ?.mapNotNull { valueJson -> valueJson.toFloat() }
                    ?.takeIf { interval -> interval.size == 2 }
                    ?.let { interval ->
                        val startRaw = interval[0]
                        val endRaw = interval[1]
                        val start = (startRaw * SECOND_MS).toLong()
                        val end = if (endRaw < 0) {
                            KwsSkipInterval.INTERVAL_UNLIMITED
                        } else {
                            (endRaw * SECOND_MS).toLong()
                        }
                        KwsSkipInterval(start, end)
                    }
            }
    }
}