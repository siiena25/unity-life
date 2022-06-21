package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.util.Tag

internal class ShowUrl(
    private val url: String,
    private val contextRepository: AssistantContextRepository,
    private val logger: Logger?
) : CancelableExecutableCommand<Unit>(logger = logger) {

    override val commandName: String = "ShowUrl"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        if (!isCancelled) {
            contextRepository.requestUrlNavigation(url)
        }
    }
}