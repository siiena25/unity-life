package ru.mail.search.assistant.commands.command.userinput.voice

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.parseAsObject
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag
import java.util.*

internal class FlowModeCommandsParser(
    private val publicCommandsFactory: PublicCommandsFactory,
    private val logger: Logger?
) {

    private companion object {

        private const val COMMAND_TYPE_EVENT = "send_event"
        private const val COMMAND_TYPE_ASR = "show_text"
    }

    private val jsonParser = JsonParser()

    @Volatile
    private var isAsrHandled = false

    fun parse(commands: List<String>, uuid: UUID): List<ExecutableCommand<*>> {
        return commands.mapNotNull { command -> parseCommand(command, uuid) }
    }

    fun isAsrHandled(): Boolean {
        return isAsrHandled
    }

    private fun parseCommand(command: String, uuid: UUID): ExecutableCommand<*>? {
        val commandJson = jsonParser.parseAsObject(command)
        if (commandJson == null) {
            logger?.e(Tag.FLOW_MODE, ResultParsingException("Failed to parse json"))
            return null
        }
        val type = commandJson.getString("type")
        if (type == null) {
            logger?.e(Tag.FLOW_MODE, ResultParsingException("Missing type field"))
            return null
        }
        return when (type) {
            COMMAND_TYPE_EVENT -> parseSendEvent(commandJson)
            COMMAND_TYPE_ASR -> parseAsr(commandJson, uuid)
            else -> null
        }
    }

    private fun parseSendEvent(commandJson: JsonObject): ExecutableCommand<*>? {
        val event = commandJson.getString("event")
        if (event == null) {
            logger?.e(Tag.FLOW_MODE, ResultParsingException("Missing event field"))
            return null
        }
        val callbackData = commandJson.getString("callback_data")
        return publicCommandsFactory.sendEvent(event = event, callbackData = callbackData)
    }

    private fun parseAsr(commandJson: JsonObject, uuid: UUID): ExecutableCommand<*>? {
        isAsrHandled = true
        val text = commandJson.getString("text")
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val message = MessageData.OutgoingData(text, uuid)
        return publicCommandsFactory.showMessage(message)
    }
}