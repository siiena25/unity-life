package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class UnknownCommand(private val logger: Logger?) :
    ExecutableCommand<Unit> {

    override val commandName: String = "UnknownCommand"

    override suspend fun execute(context: ExecutionContext) {
        logger?.w(Tag.ASSISTANT_COMMAND, "Executing unknown command")
    }
}
