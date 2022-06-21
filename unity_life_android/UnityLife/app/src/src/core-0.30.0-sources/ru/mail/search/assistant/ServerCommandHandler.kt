package ru.mail.search.assistant

import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.processor.ExecutableCommand

interface ServerCommandHandler {

    fun getCommandType(): String

    fun createCommand(
        commandsAdapter: CommandsAdapter,
        json: JsonObject
    ): ExecutableCommand<*>
}