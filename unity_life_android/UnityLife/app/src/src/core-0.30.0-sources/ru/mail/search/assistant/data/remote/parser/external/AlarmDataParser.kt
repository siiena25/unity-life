package ru.mail.search.assistant.data.remote.parser.external

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.util.getBoolean
import ru.mail.search.assistant.common.util.getLong
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CancelAlarmServerCommand
import ru.mail.search.assistant.entities.external.SetAlarmServerCommand

internal class AlarmDataParser : ExternalDataParser {

    override fun getCommandName(): String {
        return "alarm"
    }

    override fun parse(json: JsonObject): ServerCommand.ExternalUserDefined? {
        return when (json.getBoolean("cancel", false)) {
            false -> json.getLong("timestamp")
                ?.let { timestamp ->
                    SetAlarmServerCommand(timestamp)
                }
            true -> json.getLong("timestamp")
                ?.let { timestamp ->
                    CancelAlarmServerCommand(timestamp)
                }
        }
    }
}