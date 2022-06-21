package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.getFloat
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.audio.Tale

internal class TaleParser(
    private val kwsSkipIntervalsParser: KwsSkipIntervalsParser,
    private val audioSourceParser: AudioSourceParser
) {

    fun parse(jsonObject: JsonObject): Tale {
        val metaJson = jsonObject.getObject("meta")
        return Tale(
            title = metaJson?.getString("title").orEmpty(),
            coverUrl = metaJson?.getString("coverUrl").orEmpty(),
            url = jsonObject.getString("url")
                ?: throw ResultParsingException("Failed to parse tale: missing url"),
            audioSource = metaJson?.let(audioSourceParser::parse),
            seek = metaJson?.getFloat("seek"),
            kwsSkipIntervals = kwsSkipIntervalsParser.parse(jsonObject)
        )
    }
}