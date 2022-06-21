package ru.mail.search.assistant.commands.command.userinput

import ru.mail.search.assistant.commands.CommandsPhraseAdapter
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class SendPushPayload(
    private val pushId: String?,
    private val callbackData: String,
    private val phraseAdapter: CommandsPhraseAdapter,
    private val logger: Logger?
) : CancelableContextCommand<PhraseResult>(logger) {

    override suspend fun executeInContext(context: ExecutionContext): PhraseResult {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing SendPushPayload $phraseString")
        return phraseAdapter.sendPushPayload(
            context = context,
            pushId = pushId,
            callbackData = callbackData
        )
    }
}