package ru.mail.search.assistant.data.remote.parser.external

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.mail.search.assistant.data.remote.dto.external.OpenAppDto
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.OpenAppServerCommand

internal class OpenAppDataParser(private val gson: Gson) : ExternalDataParser {
    override fun getCommandName(): String {
        return "app"
    }

    override fun parse(json: JsonObject): ServerCommand.ExternalUserDefined? {
        return gson.fromJson(json.toString(), OpenAppDto::class.java)
            .run {
                OpenAppServerCommand(
                    appId,
                    path,
                    fallbackUrl.orEmpty(),
                    androidData?.intents.orEmpty()
                )
            }
    }
}