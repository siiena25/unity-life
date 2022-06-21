package ru.mail.search.assistant.commands.command.userinput

import ru.mail.search.assistant.commands.CommandsPhraseAdapter
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class SendEvent(
    private val event: String,
    private val callbackData: String?,
    private val clientData: String?,
    private val params: Map<String, String>,
    private val phraseAdapter: CommandsPhraseAdapter,
    private val logger: Logger?
) : CancelableContextCommand<PhraseResult>(logger = logger) {

    override suspend fun executeInContext(context: ExecutionContext): PhraseResult {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing SendEvent $phraseString")
        return phraseAdapter.sendEvent(
            context = context,
            event = event,
            callbackData = callbackData,
            clientData = clientData,
            params = params
        )
    }
}