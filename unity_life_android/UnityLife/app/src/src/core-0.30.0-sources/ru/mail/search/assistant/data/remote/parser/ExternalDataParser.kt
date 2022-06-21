package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.entities.ServerCommand

interface ExternalDataParser {
    fun getCommandName(): String
    fun parse(json: JsonObject): ServerCommand.ExternalUserDefined?
}