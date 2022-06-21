package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.audio.AudioSource

internal class AudioSourceParser {

    fun parse(metaJson: JsonObject): AudioSource? {
        val sourceJson = metaJson.getObject("source") ?: return null
        return AudioSource(
            mediaType = sourceJson.getString("media_type"),
            uid = sourceJson.getString("uid"),
            sourceType = sourceJson.getString("type") ?: sourceJson.getString("media_type"),
            skillName = sourceJson.getString("skill_name"),
            source = sourceJson.toString()
        )
    }
}