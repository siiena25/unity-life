package ru.mail.search.assistant.data.remote.parser.external

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.mail.search.assistant.data.remote.dto.external.CheckAppDto
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CheckAppServerCommand

internal class CheckAppDataParser(private val gson: Gson) : ExternalDataParser {
    override fun getCommandName(): String {
        return "check_app"
    }

    override fun parse(json: JsonObject): ServerCommand.ExternalUserDefined? {
        return gson.fromJson(json.toString(), CheckAppDto::class.java)
            .run {
                CheckAppServerCommand(appId, responseYes, responseNo, data?.intents.orEmpty())
            }
    }
}