package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.audio.Radiostation

internal class RadioParser(
    private val kwsSkipIntervalsParser: KwsSkipIntervalsParser,
    private val audioSourceParser: AudioSourceParser
) {

    fun parse(jsonObject: JsonObject): Radiostation {
        val metaJson = jsonObject.getObject("meta")
        return Radiostation(
            artist = metaJson?.getString("artist").orEmpty(),
            title = metaJson?.getString("title").orEmpty(),
            coverUrl = metaJson?.getString("coverUrl").orEmpty(),
            url = jsonObject.getString("url")
                ?: throw ResultParsingException("Failed to parse radio: missing url"),
            audioSource = metaJson?.let(audioSourceParser::parse),
            kwsSkipIntervals = kwsSkipIntervalsParser.parse(jsonObject)
        )
    }
}