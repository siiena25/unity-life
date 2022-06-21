package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.audio.Sound

internal class NoiseParser(
    private val kwsSkipIntervalsParser: KwsSkipIntervalsParser,
    private val audioSourceParser: AudioSourceParser
) {

    fun parse(jsonObject: JsonObject): Sound {
        val metaJson = jsonObject.getObject("meta")
        return Sound(
            name = metaJson?.getString("title").orEmpty(),
            url = jsonObject.getString("url")
                ?: throw ResultParsingException("Failed to parse noise: missing url"),
            audioSource = metaJson?.let(audioSourceParser::parse),
            kwsSkipIntervals = kwsSkipIntervalsParser.parse(jsonObject)
        )
    }
}