package ru.mail.search.assistant.data.remote.parser.external

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.util.getBoolean
import ru.mail.search.assistant.common.util.getLong
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CancelTimerServerCommand
import ru.mail.search.assistant.entities.external.SetTimerServerCommand

internal class TimerDataParser : ExternalDataParser {

    override fun getCommandName(): String {
        return "timer"
    }

    override fun parse(json: JsonObject): ServerCommand.ExternalUserDefined? {
        val cancel = json.getBoolean("cancel", false)
        return if (!cancel) {
            json.getLong("timestamp_delta")
                ?.takeIf { it > 0 }
                ?.let { timestampDelta -> SetTimerServerCommand(timestampDelta) }
        } else {
            CancelTimerServerCommand
        }
    }
}